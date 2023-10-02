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
        super(null, null);
        this.base = base;
        this.number = number;
        this.sleeve = sleeve;
        this.type = type;
        this.stripesColor = stripesColor;
        this.splitColor = splitColor;
        this.shirtType = shirtType;
        this.sleeveDetail = sleeveDetail;
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
}
