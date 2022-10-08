package net.draimcido.draimfarming.objects;


import net.draimcido.draimfarming.api.crop.Crop;
import net.draimcido.draimfarming.integrations.season.DFSeason;
import net.draimcido.draimfarming.objects.actions.ActionInterface;
import net.draimcido.draimfarming.objects.requirements.RequirementInterface;

public class DFCrop implements Crop {

    private DFSeason[] seasons;
    private RequirementInterface[] requirementInterfaces;
    private String returnStage;
    private QualityLoot qualityLoot;
    private GiganticCrop giganticCrop;
    private double skillXP;
    private OtherLoot[] otherLoots;
    private ActionInterface[] actions;
    private final String key;

    public DFCrop(String key) {
        this.key = key;
    }

    public QualityLoot getQualityLoot() {
        return qualityLoot;
    }

    public void setQualityLoot(QualityLoot qualityLoot) {
        this.qualityLoot = qualityLoot;
    }

    public String getKey() {
        return key;
    }

    public DFSeason[] getSeasons() {
        return seasons;
    }

    public RequirementInterface[] getRequirements() {
        return requirementInterfaces;
    }

    public String getReturnStage() {
        return returnStage;
    }

    public GiganticCrop getGiganticCrop() {
        return giganticCrop;
    }

    public double getSkillXP() {
        return skillXP;
    }

    public OtherLoot[] getOtherLoots() {
        return otherLoots;
    }

    public ActionInterface[] getActions() {
        return actions;
    }

    public void setSeasons(DFSeason[] seasons) {
        this.seasons = seasons;
    }

    public void setRequirements(RequirementInterface[] requirementInterfaces) {
        this.requirementInterfaces = requirementInterfaces;
    }

    public void setReturnStage(String returnStage) {
        this.returnStage = returnStage;
    }

    public void setGiganticCrop(GiganticCrop giganticCrop) {
        this.giganticCrop = giganticCrop;
    }

    public void setSkillXP(double skillXP) {
        this.skillXP = skillXP;
    }

    public void setOtherLoots(OtherLoot[] otherLoots) {
        this.otherLoots = otherLoots;
    }

    public void setActions(ActionInterface[] actions) {
        this.actions = actions;
    }
}
