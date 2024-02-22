package com.example.paint_demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.ArcType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    @FXML
    Canvas paintCanvas;
    @FXML
    Button arcButton, segmentButton, gcode;
    Double startX, startY, endX, endY;
    GraphicsContext gc;
    List<Segment> segmentList = new ArrayList<>();
    private boolean isDragging = false;

    private double centerX;
    private double centerY;
    private double radius;
    private double startAngle;
    private double endAngle;
    List<Arc> arcList = new ArrayList<>();
    private boolean isArc = false;


    public void initialize() {
        if (paintCanvas != null) {
            gc = paintCanvas.getGraphicsContext2D();
        }
        paintCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        paintCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        paintCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        paintCanvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMoved); // Adăugați eveniment de "hover"
    }
    @FXML
    public void handleArcButtonAction(ActionEvent event) {
        isArc = true;
    }
    @FXML
    public void handleSegmentButtonAction(ActionEvent event) {
        isArc = false;
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        if(!isArc) {
            double mouseX = event.getX();
            double mouseY = event.getY();
            boolean isHover = false;
            for (Segment segment : segmentList) {
                if (isMouseOverSegmentStart(segment, mouseX, mouseY)) {
                    mouseX = segment.getStartX();
                    mouseY = segment.getStartY();
                    isHover = true;
                } else if (isMouseOverSegmentEnd(segment, mouseX, mouseY)) {
                    mouseX = segment.getEndX();
                    mouseY = segment.getEndY();
                    isHover = true;
                }
            }

            if (isHover) {
                startX = mouseX;
                startY = mouseY;
            } else {
                startX = event.getX();
                startY = event.getY();
            }
        }else if(isArc){
            //codul pentru arc
            centerX = event.getX();
            centerY = event.getY();
            radius = 0;
            startAngle = 0;
            endAngle = 0;
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        if(!isArc) {
            isDragging = true;
            gc.clearRect(0, 0, paintCanvas.getWidth(), paintCanvas.getHeight());
            drawPaint();
            endX = event.getX();
            endY = event.getY();
            drawLine();
        }
        else if(isArc){
            //codul pentru arc
            isDragging = true;
            gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
            drawPaint();
            radius = calculateRadius(centerX, centerY, event.getX(), event.getY());
            startAngle = calculateAngle(centerX, centerY, centerX, centerY);
            endAngle = calculateAngle(centerX, centerY, event.getX(), event.getY());
            drawArc();
        }
    }

    @FXML
    private void handleMouseReleased(MouseEvent event) {
        if(!isArc) {
            if (!isDragging) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                boolean isHover = false;
                for (Segment segment : segmentList) {
                    if (isMouseOverSegmentStart(segment, mouseX, mouseY)) {
                        mouseX = segment.getStartX();
                        mouseY = segment.getStartY();
                        isHover = true;
                    } else if (isMouseOverSegmentEnd(segment, mouseX, mouseY)) {
                        mouseX = segment.getEndX();
                        mouseY = segment.getEndY();
                        isHover = true;
                    }
                }

                if (isHover) {
                    endX = mouseX;
                    endY = mouseY;
                } else {
                    endX = event.getX();
                    endY = event.getY();
                }

                segmentSaves();
                drawLine();
            }
            isDragging = false; // Resetăm starea
        }else if(isArc){
            if(!isDragging) {
                //codul pentru arc
                endAngle = calculateAngle(centerX, centerY, event.getX(), event.getY());
                arcSaves();
                drawArc();
            }
            isDragging = false;
        }
    }

    @FXML
    private void handleMouseMoved(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        gc.clearRect(0, 0, paintCanvas.getWidth(), paintCanvas.getHeight()); // Șterge tot
        drawPaint();
        // Verificați dacă mouse-ul face hover pe capătul de la început al segmentelor
        for (Segment segment : segmentList) {
            if (isMouseOverSegmentStart(segment, mouseX, mouseY)) {
                // Faceți ceva când mouse-ul face hover pe capătul de la început al segmentului
                // De exemplu, puteți afișa un cerc mai mare la coordonatele capătului de la început
                double startX = segment.getStartX();
                double startY = segment.getStartY();
                gc.fillOval(startX - 5, startY - 5, 10,10);// Afișează un cerc mai mare
            } else if (isMouseOverSegmentEnd(segment, mouseX, mouseY)) {
                // Faceți ceva când mouse-ul face hover pe capătul de la sfârșit al segmentului
                // De exemplu, puteți afișa un cerc mai mare la coordonatele capătului de la sfârșit
                double endX = segment.getEndX();
                double endY = segment.getEndY();
                gc.fillOval(endX - 5, endY - 5, 10, 10); // Afișează un cerc mai mare
            }else {
                // gc.clearRect(0, 0, paintCanvas.getWidth(), paintCanvas.getHeight());
                //drawPaint();
            }
        }
    }


    private boolean isMouseOverSegmentStart(Segment segment, double mouseX, double mouseY) {
        double startSegmentX = segment.getStartX();
        double startSegmentY = segment.getStartY();
        double distance = Math.sqrt(Math.pow(mouseX - startSegmentX, 2) + Math.pow(mouseY - startSegmentY, 2));
        double threshold = 5;  //valoare pentru zona "hover"

        return distance < threshold;
    }


    private boolean isMouseOverSegmentEnd(Segment segment, double mouseX, double mouseY) {
        double endSegmentX = segment.getEndX();
        double endSegmentY = segment.getEndY();
        double distance = Math.sqrt(Math.pow(mouseX - endSegmentX, 2) + Math.pow(mouseY - endSegmentY, 2));
        double threshold = 5; //valoare pentru zona "hover"

        return distance < threshold;
    }

    private void drawLine() {
        gc.strokeLine(startX, startY, endX, endY);
    }

    private void segmentSaves() {
        Segment segment = new Segment(startX, startY, endX, endY);
        segmentList.add(segment);
        System.out.println("Segment: "+ segment.getStartX() + " " + segment.getStartY());
    }

    private void arcSaves() {
        double length = endAngle - startAngle;
        double endX = centerX + radius * Math.cos(Math.toRadians(startAngle + length));
        double endY = centerY + radius * Math.sin(Math.toRadians(startAngle + length));
        double startX = centerX + radius * Math.cos(Math.toRadians(startAngle));
        double startY = centerY + radius * Math.sin(Math.toRadians(startAngle));

        Arc arc = new Arc(startX, startY, endX, endY, centerX, centerY, radius, startAngle, endAngle);
        System.out.println("Arc: " + arc.getStartX() + " " + arc.getStartY());

        arcList.add(arc);
    }



    private void drawPaint() {
        for (Segment segment : segmentList) {
            gc.strokeLine(segment.getStartX(), segment.getStartY(), segment.getEndX(), segment.getEndY());
        }
        for (Arc arc : arcList){
            gc.strokeArc(arc.getCenterX() - arc.getRadius(), arc.getCenterY() - arc.getRadius(), 2 * arc.getRadius(), 2 * arc.getRadius(), arc.getStartAngle(), arc.getEndAngle() - arc.getStartAngle(), ArcType.OPEN);
        }
    }

    //metode arce
    private double calculateRadius(double centerX, double centerY, double x, double y) {
        return Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
    }

    private double calculateAngle(double centerX, double centerY, double x, double y) {
        double angle = Math.toDegrees(Math.atan2(y - centerY, x - centerX));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }
    private void drawArc() {
        gc.strokeArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, startAngle, endAngle - startAngle, ArcType.OPEN);
    }



    public void generateGCode(ActionEvent event) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\pinte\\Desktop\\An3_sem1\\SSC\\Proiect\\CNC_control\\Schite\\G_CODE.txt"
        ))) {

            for (Segment segment : segmentList) {
                String gCodeSegment = generateSegmentGCode(segment);
                writer.write(gCodeSegment);
                writer.newLine();
            }

            for (Arc arc : arcList) {
                String gCodeArc = generateArcGCode(arc);
                writer.write(gCodeArc);
                writer.newLine();
            }

            writer.write("\nG1 X0.00 Y0.00");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateSegmentGCode(Segment segment) {
        double startX = segment.getStartX();
        double startY = segment.getStartY();
        double endX = segment.getEndX();
        double endY = segment.getEndY();

        return "\nM300 S50"+ String.format("\nG1 X%.2f Y%.2f", startX/10, startY/10) +
                "\nG4 P300" + // Wait 150ms
                "\nM300 S30" + // Pen down
                String.format("\nG1 X%.2f Y%.2f", endX/10, endY/10)+
                "\nM300 S50"; // Pen up
    }


    private String generateArcGCode(Arc arc) {
        double centerX = arc.getCenterX();
        double centerY = arc.getCenterY();
        double radius = arc.getRadius();
        double startAngle = arc.getStartAngle();
        double endAngle = arc.getEndAngle();
        double startX = arc.getStartX();
        double startY = arc.getStartY();
        double endX = arc.getEndX();
        double endY = arc.getEndY();

        StringBuilder coordonateArc = new StringBuilder();

        for (double angle = startAngle; angle <= endAngle; angle += 20.0) {
            // Convertim unghiul în radiani pentru funcțiile trigonometrice
            double radians = Math.toRadians(angle);

            // Calculăm coordonatele (X, Y) pentru fiecare punct de pe arc
            double x = centerX + radius * Math.cos(radians);
            double y = centerY + radius * Math.sin(radians);

            // Adăugăm coordonatele punctului în șirul StringBuilder
            coordonateArc.append(String.format("\nG1 X%.2f Y%.2f F3500.00", x/10, y/10));
        }

        return "\nM300 S50" +
                String.format("\nG1 X%.2f Y%.2f", startX/10, startY/10) +
                "\nM300 S30" +
                coordonateArc.toString() + // Adăugăm coordonatele calculate în șir
                "\nM300 S50"; // Ridică stiloul
    }


}


