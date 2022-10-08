package net.draimcido.draimfarming.api.crop;

import net.draimcido.draimfarming.integrations.season.DFSeason;
import net.draimcido.draimfarming.objects.GiganticCrop;
import net.draimcido.draimfarming.objects.OtherLoot;
import net.draimcido.draimfarming.objects.QualityLoot;
import net.draimcido.draimfarming.objects.actions.ActionInterface;
import net.draimcido.draimfarming.objects.requirements.RequirementInterface;

public interface Crop {

    DFSeason[] getSeasons();

    RequirementInterface[] getRequirements();

    String getReturnStage();

    QualityLoot getQualityLoot();

    GiganticCrop getGiganticCrop();

    double getSkillXP();

    OtherLoot[] getOtherLoots();

    ActionInterface[] getActions();

    String getKey();
}