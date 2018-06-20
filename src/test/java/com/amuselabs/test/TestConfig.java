package com.amuselabs.test;

/**
 * Created by charu on 12/28/17.
 */
public class TestConfig
{
    public String crosswordLink="http://amuselabs.com/pmm/crossword?id=tca171226&set=wapo-daily";
    public boolean embedded=false;
    public boolean cookiesDisabled=false;
    public boolean rebus=false;
    public int puzzleType=0; //0 for WaPo, 1 for pmm, 2 for NewsDay
    public boolean startButtonPresent=true;
    public String firstWordAcross="MIMOSA";
    public String firstWordDown="MBA";
    public String lastWordAcross="ASSETS";
    public String lastWordDown="DTS";
    public String secondWordAcross="ULNA";
    public String entireGridSequence="MIMOSAULNAEFTBRACEDFAILALEAKITASCOCOATEASTERTHUNDERGOATCHEESEDICEAMIHEAVEPOREDLESEPSIORTSSEATCOVERPITTTAEABRAPINEDSETTOREXOLAFREDHOTCOALRETAPESHOWLTURNCOATLEONIDIPATIMEARTISTASPSLEDASSETS";
    public TestConfig(){
    }
    public TestConfig(String crosswordLink, boolean embedded, boolean cookiesDisabled, boolean rebus, int puzzleType, boolean startButtonPresent, String firstWordAcross, String firstWordDown, String secondWordAcross, String entireGridSequence){
        this.crosswordLink=crosswordLink;
        this.embedded=embedded;
        this.cookiesDisabled=cookiesDisabled;
        this.rebus=rebus;
        this.puzzleType=puzzleType;
        this.startButtonPresent=startButtonPresent;
        this.firstWordAcross=firstWordAcross;
        this.firstWordDown=firstWordDown;
        this.secondWordAcross=secondWordAcross;
        this.entireGridSequence=entireGridSequence;
    }
}
