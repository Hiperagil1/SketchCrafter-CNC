package com.example.paint_demo;

public class Arc {
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;
    private Double centerX;
    private Double centerY;
    private Double radius;
    private Double startAngle;
    private Double endAngle;

    public Arc(Double startX, Double startY, Double endX, Double endY, Double centerX, Double centerY, Double radius, Double startAngle, Double endAngle){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getRadius() {
        return radius;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public double getEndAngle() {
        return endAngle;
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
