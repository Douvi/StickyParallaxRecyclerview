package com.dovi.stickyparallaxrecyclerview.src;

public class Section {
    private int sectionId;
    private int headerPosition;
    private int startRow;
    private int numberOfRow;
    private boolean showSection;

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return this.startRow + this.numberOfRow;
    }

    public int getNumberOfRow() {
        return numberOfRow;
    }

    public void setNumberOfRow(int numberOfRow) {
        this.numberOfRow = numberOfRow;
    }

    public int getHeaderPosition() {
        return headerPosition;
    }

    public void setHeaderPosition(int headerPosition) {
        this.headerPosition = headerPosition;
    }

    public boolean isShowSection() {
        return showSection;
    }

    public void setShowSection(boolean showSection) {
        this.showSection = showSection;
    }
}