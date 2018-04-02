/*
 * Copyright (c) 2018 Chris Swenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package biggerfish.gui;

import cage.core.application.Timer;
import cage.core.gui.component.GUIComponent;
import cage.core.gui.graphics.GUIGraphics;
import cage.core.gui.graphics.TextAlign;

public class FPSMonitor extends GUIComponent {

    private Timer timer;
    private float x;
    private float y;
    private float width;
    private float height;
    private int fontSize;
    private float margin;
    private int[] fpsHistory;
    private int fpsIndex;
    private int fpsMin;
    private int fpsMax;
    private int fpsCurrent;
    private int msCurrent;
    private int frames;
    private float graphHeight;
    
    public FPSMonitor(Timer timer) {
        this.timer = timer;
        this.x = 4.0f;
        this.y = 4.0f;
        this.width = 80.0f;
        this.height = 48.0f;
        this.fontSize = 10;
        this.margin = 3.0f;
        this.fpsHistory = new int[(int)(width - margin * 2)];
        this.fpsIndex = 0;
        this.fpsMin = 10000;
        this.fpsMax = 0;
        this.fpsCurrent = 0;
        this.msCurrent = 0;
        this.frames = 0;
        this.graphHeight = height - margin * 3.0f - fontSize;
    }
    
    public void update(float deltaTime) {
        msCurrent = (int)(deltaTime * 1000.0f);
        if(timer.getElapsedTime() < 1.0f) {
            frames++;
        }
        else {
            timer.reset();
            fpsCurrent = frames;
            frames = 0;
            fpsMin = Math.min(fpsMin, fpsCurrent);
            fpsMax = Math.max(fpsMax, fpsCurrent);
            fpsHistory[fpsIndex++] = fpsCurrent;
            if(fpsIndex >= fpsHistory.length) {
                fpsIndex = 0;
            }
        }
    }
        
    @Override
    public void render(GUIGraphics g) {
        g.setFill(26.0f / 255.0f, 26.0f / 255.0f, 56.0f / 255.0f, 0.9f);
        g.beginPath();
        g.rect(x, y, width, height);
        g.closePath();
        g.fill();

        g.setFill(26.0f / 255.0f, 1.0f, 1.0f, 0.05f);
        g.beginPath();
        g.rect(x + margin, y + margin * 2.0f + fontSize, width - margin * 2.0f, graphHeight);
        g.closePath();
        g.fill();

        g.setFill(26.0f / 255.0f, 1.0f, 1.0f, 1.0f);
        g.beginPath();
        for(int i=0; i<fpsHistory.length; ++i) {
            float fps = fpsHistory[i];
            float percent = Math.min(1.0f, Math.max(0.0f, (fps - fpsMin) / (fpsMax - fpsMin)));
            g.rect(x + margin + i, y + margin * 2.0f + fontSize + (1.0f - percent) * graphHeight, 1.0f, percent * graphHeight);
        }
        g.closePath();
        g.fill();

        g.setFont("Arial");
        g.setTextAlign(TextAlign.TOP);
        g.setFontSize(fontSize);
        g.drawText(x + margin, y + margin, fpsCurrent + " FPS / " + msCurrent + " MS");
    }
}
