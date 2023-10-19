package com.example.paint_demo;

public class Segment {
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;

    public Segment(Double startX, Double startY, Double endX, Double endY){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public Double getStartX() {
        return startX;
    }

    public Double getStartY() {
        return startY;
    }

    public Double getEndX() {
        return endX;
    }

    public Double getEndY() {
        return endY;
    }
}
