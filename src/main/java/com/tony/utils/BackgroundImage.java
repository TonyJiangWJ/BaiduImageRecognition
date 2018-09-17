/*
 * Copyright 2015 Alan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tony.utils;

import javax.swing.*;
import java.awt.*;

/**
 * 显示图片的Label
 *
 * @author Alan
 * copyed from https://gitee.com/alanzyy/ScreenCapture.git
 */
class BackgroundImage extends JLabel {

    private int lineX;
    private int lineY;
    private Rectangle oldRect;
    private int x;
    private int y;
    private int h;
    private int w;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //画十字交叉线，交叉点为截屏起点
        g.drawLine(lineX, 0, lineX, getHeight());
        g.drawLine(0, lineY, getWidth(), lineY);

        if (w > 0 && h > 0) {
            //画截屏范围
            g.drawRect(x, y, w, h);
            //画截屏的大小
            String area = Integer.toString(w) + " * " + Integer.toString(h);
            g.drawString(area, x + (int) w / 2 - 15, y + (int) h / 2);
        }

        if (oldRect != null && oldRect.width > 0 && oldRect.height > 0) {
            g.setColor(Color.GRAY);
            //画旧截屏范围
            g.drawRect(oldRect.x, oldRect.y, oldRect.width, oldRect.height);
            //画旧截屏的大小
            String area = Integer.toString(oldRect.width) + " * " + Integer.toString(oldRect.height);
            g.drawString(area, oldRect.x + (int) oldRect.width / 2 - 15, oldRect.y + (int) oldRect.height / 2);
        }

    }

    public void setOldRectangle(Rectangle r) {
        this.oldRect = r;
    }

    public void drawRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.h = height;
        this.w = width;
        repaint();
    }

    public void drawCross(int x, int y) {
        this.lineX = x;
        this.lineY = y;
        repaint();
    }

    public void reset() {
        this.x = -1;
        this.y = -1;
        this.w = -1;
        this.h = -1;
    }
}
