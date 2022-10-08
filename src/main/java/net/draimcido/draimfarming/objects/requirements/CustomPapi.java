package net.draimcido.draimfarming.objects.requirements;

import net.draimcido.draimfarming.objects.requirements.papi.*;
import net.draimcido.draimfarming.utils.AdventureUtil;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CustomPapi implements RequirementInterface {

    public static HashSet<String> allPapi = new HashSet<>();

    private PapiRequirement papiRequirement;
    private final String msg;

    public CustomPapi(Map<String, Object> expressions, String msg){
        this.msg = msg;
        expressions.keySet().forEach(key -> {
            if (key.startsWith("&&")){
                List<PapiRequirement> papiRequirements = new ArrayList<>();
                if (expressions.get(key) instanceof MemorySection map2){
                    addAndRequirements(papiRequirements, map2.getValues(false));
                }
                papiRequirement = new ExpressionAnd(papiRequirements);
            }
            else if (key.startsWith("||")){
                List<PapiRequirement> papiRequirements = new ArrayList<>();
                if (expressions.get(key) instanceof MemorySection map2){
                    addOrRequirements(papiRequirements, map2.getValues(false));
                }
                papiRequirement = new ExpressionOr(papiRequirements);
            }
            else {
                if (expressions.get(key) instanceof MemorySection map){
                    String type = map.getString("type");
                    if (type == null) return;
                    String papi = map.getString("papi");
                    if (papi == null) return;
                    String value = map.getString("value");
                    if (value == null) return;
                    allPapi.add(papi);
                    switch (type){
                        case "==" -> papiRequirement = new PapiEquals(papi, value);
                        case "!=" -> papiRequirement = new PapiNotEquals(papi, value);
                        case ">=" -> papiRequirement = new PapiNoLess(papi, Double.parseDouble(value));
                        case "<=" -> papiRequirement = new PapiNoLarger(papi, Double.parseDouble(value));
                        case "<" -> papiRequirement = new PapiSmaller(papi, Double.parseDouble(value));
                        case ">" -> papiRequirement = new PapiGreater(papi, Double.parseDouble(value));
                    }
                }
            }
        });
    }

    @Override
    public boolean isConditionMet(PlantingCondition plantingCondition) {
        if (!papiRequirement.isMet(plantingCondition.getPapiMap())) {
            AdventureUtil.playerMessage(plantingCondition.getPlayer(), msg);
            return false;
        }
        return true;
    }

    private void addAndRequirements(List<PapiRequirement> requirements, Map<String, Object> map){
        requirements.add(new ExpressionAnd(getRequirements(map)));
    }

    private void addOrRequirements(List<PapiRequirement> requirements, Map<String, Object> map){
        requirements.add(new ExpressionOr(getRequirements(map)));
    }

    private List<PapiRequirement> getRequirements(Map<String, Object> map) {
        List<PapiRequirement> papiRequirements = new ArrayList<>();
        map.keySet().forEach(key -> {
            if (key.startsWith("&&")){
                if (map.get(key) instanceof MemorySection map2){
                    addAndRequirements(papiRequirements, map2.getValues(false));
                }
            }else if (key.startsWith("||")){
                if (map.get(key) instanceof MemorySection map2){
                    addOrRequirements(papiRequirements, map2.getValues(false));
                }
            }else {
                if (map.get(key) instanceof MemorySection map2){
                    String type = map2.getString("type");
                    if (type == null) return;
                    String papi = map2.getString("papi");
                    if (papi == null) return;
                    String value = map2.getString("value");
                    if (value == null) return;
                    allPapi.add(papi);
                    switch (type){
                        case "==" -> papiRequirements.add(new PapiEquals(papi, value));
                        case "!=" -> papiRequirements.add(new PapiNotEquals(papi, value));
                        case ">=" -> papiRequirements.add(new PapiNoLess(papi, Double.parseDouble(value)));
                        case "<=" -> papiRequirements.add(new PapiNoLarger(papi, Double.parseDouble(value)));
                        case "<" -> papiRequirements.add(new PapiSmaller(papi, Double.parseDouble(value)));
                        case ">" -> papiRequirements.add(new PapiGreater(papi, Double.parseDouble(value)));
                    }
                }
            }
        });
        return papiRequirements;
    }
}
