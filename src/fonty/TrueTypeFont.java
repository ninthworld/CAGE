package fonty;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;

public class TrueTypeFont {

    private BinaryReader reader;
    private HashMap<String, Table> tables;

    private int scalarType;
    private int searchRange;
    private int entrySelector;
    private int rangeShift;

    private int version;
    private int fontRevision;
    private int checksumAdjustment;
    private int magicNumber;
    private int flags;
    private int unitsPerEm;
    private Date created;
    private Date modified;
    private int xMin;
    private int yMin;
    private int xMax;
    private int yMax;
    private int macStyle;
    private int lowestRecPPEM;
    private int fontDirectionHint;
    private int indexToLocFormat;
    private int glyphDataFormat;

    public TrueTypeFont(ByteBuffer buffer) {
        this.reader = new BinaryReader(buffer);
        this.tables = new HashMap<>();
        readOffsetTables();
        readHeadTable();
    }

    private void readOffsetTables() {
        scalarType = reader.getUint32();
        int numTables = reader.getUint16();
        searchRange = reader.getUint16();
        entrySelector = reader.getUint16();
        rangeShift = reader.getUint16();

        for(int i=0; i<numTables; ++i) {
            String tag = reader.getString(4);
            Table table = new Table();
            table.checksum = reader.getUint32();
            table.offset = reader.getUint32();
            table.length = reader.getUint32();
            tables.put(tag, table);

            if(!tag.equals("head")) {
                assert calculateChecksum(table.offset, table.length) == table.checksum;
            }
        }
    }

    private int calculateChecksum(int offset, int length) {
        int old = reader.getPosition();
        reader.setPosition(offset);
        int sum = 0;
        int nlongs = (length + 3) / 4;
        while(nlongs-- > 0) {
            sum += reader.getUint32();
        }
        reader.setPosition(old);
        return sum;
    }

    private void readHeadTable() {
        assert tables.containsKey("head");
        reader.setPosition(tables.get("head").offset);

        version = reader.getFixed();
        fontRevision = reader.getFixed();
        checksumAdjustment = reader.getUint32();
        magicNumber = reader.getUint32();
        assert magicNumber == 0x5f0f3cf5;
        flags = reader.getUint16();
        unitsPerEm = reader.getUint16();
        created = reader.getDate();
        modified = reader.getDate();
        xMin = reader.getFword();
        yMin = reader.getFword();
        xMax = reader.getFword();
        yMax = reader.getFword();
        macStyle = reader.getUint16();
        lowestRecPPEM = reader.getUint16();
        fontDirectionHint = reader.getInt16();
        indexToLocFormat = reader.getInt16();
        glyphDataFormat = reader.getInt16();
    }

    private int getGlyphOffset(int index) {
        assert tables.containsKey("loca");
        Table loca = tables.get("loca");
        assert tables.containsKey("glyf");
        Table glyf = tables.get("glyf");

        int offset = 0;
        int old = reader.getPosition();
        if(indexToLocFormat == 1) {
            reader.setPosition(loca.offset + index * 4);
            offset = reader.getUint32();
        }
        else {
            reader.setPosition(loca.offset + index * 2);
            offset = reader.getUint16() * 2;
        }
        reader.setPosition(old);
        return offset + glyf.offset;
    }

    public Glyph readGlyph(int index) {
        assert tables.containsKey("glyf");
        Table glyf = tables.get("glyf");

        int offset = getGlyphOffset(index);
        if(offset >= glyf.offset + glyf.length) {
            return null;
        }

        assert offset >= glyf.offset;
        assert offset < glyf.offset + glyf.length;

        reader.setPosition(offset);

        Glyph glyph = new Glyph();
        glyph.numberOfContours = reader.getInt16();
        glyph.xMin = reader.getFword();
        glyph.yMin = reader.getFword();
        glyph.xMax = reader.getFword();
        glyph.yMax = reader.getFword();

        assert glyph.numberOfContours >= -1;

        if(glyph.numberOfContours == -1) {
            // TODO: Implement
            // readCompoundGlyph(glyph);
        }
        else {
            readSimpleGlyph(glyph);
        }

        return glyph;
    }

    private void readSimpleGlyph(Glyph glyph) {
        final int ON_CURVE = 1;
        final int X_IS_BYTE = 2;
        final int Y_IS_BYTE = 4;
        final int REPEAT = 8;
        final int X_DELTA = 16;
        final int Y_DELTA = 32;

        glyph.type = GlyphType.SIMPLE;
        glyph.contourEnds = new int[glyph.numberOfContours];
        int maxCount = 0;
        for(int i=0; i<glyph.contourEnds.length; ++i) {
            glyph.contourEnds[i] = reader.getUint16();
            maxCount = Math.max(maxCount, glyph.contourEnds[i]);
        }

        reader.setPosition(reader.getUint16() + reader.getPosition());

        if(glyph.numberOfContours == 0) {
            return;
        }

        int numPoints = maxCount + 1;
        glyph.points = new Point[numPoints];
        int[] flags = new int[numPoints];
        for(int i=0; i<numPoints; ++i) {
            int flag = reader.getUint8();
            flags[i] = flag;
            glyph.points[i] = new Point();
            glyph.points[i].onCurve = (flag & ON_CURVE) > 0;

            if((flag & REPEAT) > 0) {
                int count = reader.getUint8();
                assert count > 0;
                for(int j=1; j<=count; ++j) {
                    flags[i + j] = flag;
                    glyph.points[i + j] = new Point();
                    glyph.points[i + j].onCurve = glyph.points[i].onCurve;
                }
                i += count;
            }
        }

        for(int xy=0; xy<2; ++xy) {
            int byteFlag = (xy == 0 ? X_IS_BYTE : Y_IS_BYTE);
            int deltaFlag = (xy == 0 ? X_DELTA : Y_DELTA);
            int min = (xy == 0 ? glyph.xMin : glyph.yMin);
            int max = (xy == 0 ? glyph.xMax : glyph.yMax);
            int value = 0;
            for(int i=0; i<numPoints; ++i) {
                int flag = flags[i];
                if((flag & byteFlag) > 0) {
                    if((flag & deltaFlag) > 0) {
                        value += reader.getUint8();
                    }
                    else {
                        value -= reader.getUint8();
                    }
                }
                else if((~flag & deltaFlag) > 0) {
                    value += reader.getInt16();
                }

                if(xy == 0) {
                    glyph.points[i].x = value;
                }
                else {
                    glyph.points[i].y = value;
                }
            }
        }
    }

    private class Table {
        public int checksum;
        public int offset;
        public int length;
    }

    public class Glyph {
        public int numberOfContours;
        public int xMin;
        public int yMin;
        public int xMax;
        public int yMax;
        public GlyphType type;
        public int[] contourEnds;
        public Point[] points;
    }

    public class Point {
        public int x;
        public int y;
        public boolean onCurve;
    }

    public enum GlyphType {
        SIMPLE, COMPOUND;
    }

    private class BinaryReader {

        private ByteBuffer buffer;

        public BinaryReader(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        public void setPosition(int pos) {
            assert pos < buffer.limit();
            buffer.position(pos);
        }

        public int getPosition() {
            return buffer.position();
        }

        public int getUint8() {
            return (0x000000ff & buffer.get());
        }

        public int getUint16() {
            return (getUint8() << 8) | getUint8();
        }

        public int getUint32() {
            return getInt32();
        }

        public int getInt16() {
            int val = getUint16();
            if((val & 0x8000) != 0) {
                val -= (1 << 16);
            }
            return val;
        }

        public int getInt32() {
            return (getUint16() << 16) | getUint16();
        }

        public int getFword() {
            return getInt16();
        }

        public int get2Dot14() {
            return getInt16() / (1 << 14);
        }

        public int getFixed() {
            return getInt32() / (1 << 16);
        }

        public String getString(int length) {
            StringBuilder str = new StringBuilder();
            for(int i=0; i<length; ++i) {
                str.append(Character.toString((char) getUint8()));
            }
            return str.toString();
        }

        public Date getDate() {
            long macTime = getUint32() * 0x100000000L + getUint32();
            long utcTime = macTime * 1000 - 2082844800L;
            return new Date(utcTime);
        }
    }
}
