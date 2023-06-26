package cz.kuba1428.coincraftcore.coincraftcore.other;

import java.util.ArrayList;
import java.util.HashMap;

public interface VoteStorage {
    HashMap<String, ArrayList<Integer>> hlasovaniData = new HashMap<>();
    HashMap<String, ArrayList<String>> hlasujici = new HashMap<>();
}
