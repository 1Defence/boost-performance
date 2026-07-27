package com.boostperformance;

public class InfoLabel extends CustomLabel
{
    private final String INFO_LABEL_HEXCOLOR = "#969696";
    private final String INFO_VALUE_HEXCOLOR = "#C6C6C6";

    private String label;

    boolean hasLabel = false;

    public void setInfo(String label, String value){
        this.label = label;
        hasLabel = true;
        setText("<html>"+ wrapHTMLColor(label,INFO_LABEL_HEXCOLOR)+ wrapHTMLColor(value,INFO_VALUE_HEXCOLOR)+"</html>");
    }

    public void setInfoNoLabel(String value){
        this.label = "";
        hasLabel = false;
        setText("<html>"+wrapHTMLColor(value,INFO_VALUE_HEXCOLOR)+"</html>");
    }

    public void setValue(String value){
        if(hasLabel){
            setText("<html>"+ wrapHTMLColor(label,INFO_LABEL_HEXCOLOR)+ wrapHTMLColor(value,INFO_VALUE_HEXCOLOR)+"</html>");
        }else{
            setText("<html>"+wrapHTMLColor(value,INFO_VALUE_HEXCOLOR)+"</html>");
        }
    }

    public String wrapHTMLColor(String text, String hexColor){
        return "<font color='"+hexColor+"'>"+text+"</font>";
    }
}
