/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableJerseyCi extends ExportableCi {

    private String base;
    private String number;
    private String sleeve;
    private String type;
    private String stripesColor;
    private String splitColor;
    private String shirtType;
    private String sleeveDetail;
    private Boolean stripes;
    private Boolean horizontalStripes;
    private String horizontalStripesColor;
    private Boolean squares;
    private String squaresColor;
    private Boolean split;

    public ExportableJerseyCi(
        String base,
        String number,
        String sleeve,
        String type,
        String stripesColor,
        String splitColor,
        String shirtType,
        String sleeveDetail
    ) {
        this(
            base,
            number,
            sleeve,
            type,
            stripesColor,
            splitColor,
            shirtType,
            sleeveDetail,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }

    public ExportableJerseyCi(
        String base,
        String number,
        String sleeve,
        String type,
        String stripesColor,
        String splitColor,
        String shirtType,
        String sleeveDetail,
        Boolean stripes,
        Boolean horizontalStripes,
        String horizontalStripesColor,
        Boolean squares,
        String squaresColor,
        Boolean split
    ) {
        super(null, null);
        setBase(base);
        setNumber(number);
        setSleeve(sleeve);
        setType(type);
        setStripesColor(stripesColor);
        setSplitColor(splitColor);
        setShirtType(shirtType);
        setSleeveDetail(sleeveDetail);
        setStripes(stripes);
        setHorizontalStripes(horizontalStripes);
        setHorizontalStripesColor(horizontalStripesColor);
        setSquares(squares);
        setSquaresColor(squaresColor);
        setSplit(split);
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSleeve() {
        return sleeve;
    }

    public void setSleeve(String sleeve) {
        this.sleeve = sleeve;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStripesColor() {
        return stripesColor;
    }

    public void setStripesColor(String stripesColor) {
        this.stripesColor = stripesColor;
    }

    public String getSplitColor() {
        return splitColor;
    }

    public void setSplitColor(String splitColor) {
        this.splitColor = splitColor;
    }

    public String getShirtType() {
        return shirtType;
    }

    public void setShirtType(String shirtType) {
        this.shirtType = shirtType;
    }

    public String getSleeveDetail() {
        return sleeveDetail;
    }

    public void setSleeveDetail(String sleeveDetail) {
        this.sleeveDetail = sleeveDetail;
    }

    public Boolean getStripes() {
        return stripes;
    }

    public void setStripes(Boolean stripes) {
        this.stripes = stripes;
    }

    public Boolean getHorizontalStripes() {
        return horizontalStripes;
    }

    public void setHorizontalStripes(Boolean horizontalStripes) {
        this.horizontalStripes = horizontalStripes;
    }

    public String getHorizontalStripesColor() {
        return horizontalStripesColor;
    }

    public void setHorizontalStripesColor(String horizontalStripesColor) {
        this.horizontalStripesColor = horizontalStripesColor;
    }

    public Boolean getSquares() {
        return squares;
    }

    public void setSquares(Boolean squares) {
        this.squares = squares;
    }

    public String getSquaresColor() {
        return squaresColor;
    }

    public void setSquaresColor(String squaresColor) {
        this.squaresColor = squaresColor;
    }

    public Boolean getSplit() {
        return split;
    }

    public void setSplit(Boolean split) {
        this.split = split;
    }
}
