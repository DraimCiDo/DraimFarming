package net.draimcido.draimfarming.objects.requirements.papi;


import java.util.HashMap;

public interface PapiRequirement {
    boolean isMet(HashMap<String, String> papiMap);
}
