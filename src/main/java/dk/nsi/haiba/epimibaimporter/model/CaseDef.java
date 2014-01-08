package dk.nsi.haiba.epimibaimporter.model;

public class CaseDef {
    private int aId;
    private String aText;

    public int getId() {
        return aId;
    }

    @Override
    public String toString() {
        return "CaseDef [aId=" + aId + ", aText=" + aText + "]";
    }

    public void setId(int id) {
        aId = id;
    }

    public String getText() {
        return aText;
    }

    public void setText(String text) {
        aText = text;
    }
}
