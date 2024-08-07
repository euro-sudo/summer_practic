package model;

import java.util.ArrayList;
import java.util.List;

public class Remind {
    private int id;
    private String text;
    private List<String> photos;
    private float textSize;
    private int backgroundColor;

    // НАЧАЛО добавляем конструкторы в класс
    public Remind() {
    }
    public Remind(int id, String text, int backgroundColor, float textSize, List<String> photos) {
        this.id = id;
        this.text = text;
        this.backgroundColor = backgroundColor;
        this.textSize = textSize;
        this.photos = photos;
    }
    public Remind(String text, int backgroundColor, float textSize, List<String> photos) {
        this.text = text;
        this.backgroundColor = backgroundColor;
        this.textSize = textSize;
        this.photos = photos;
    }

    // КОНЕЦ добавляем конструкторы в класс


    // НАЧАЛО Добавляем геттеры и сеттеры

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public List<String> getPhotos() {
        return photos;
    }
    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
    public void addPhoto(String photo) {
        if (this.photos == null) {
            this.photos = new ArrayList<>();
        }
        this.photos.add(photo);
    }

    public float getTextSize() {
        return textSize;
    }
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getBackColor() {
        return backgroundColor;
    }
    public void setBackColor(int backColor) {
        this.backgroundColor = backColor;
    }
    // КОНЕЦ Добавляем геттеры и сеттеры
}
