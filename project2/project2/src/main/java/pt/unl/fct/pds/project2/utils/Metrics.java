package pt.unl.fct.pds.project2.utils;
import pt.unl.fct.pds.project2.model.Circuit;
import pt.unl.fct.pds.project2.model.Node;
import java.util.*;

public class Metrics {
    
    private Set<String> globalNodes; //guarda as fingerprints dos nodes
    private Set<String> guardNodes; 
    private Set<String> middleNodes;    
    private Set<String> exitNodes;
    
    //entropia de nodes
    private Map<String, Integer> globalNodeFrequency;
    private Map<String, Integer> guardNodeFrequency;
    private Map<String, Integer> middleNodeFrequency;
    private Map<String, Integer> exitNodeFrequency;
    
    //entropia de paises
    private Map<String, Integer> globalCountryFrequency;
    private Map<String, Integer> guardCountryFrequency;
    private Map<String, Integer> middleCountryFrequency;
    private Map<String, Integer> exitCountryFrequency;
    
    //lista bandwidth dos circuitos
    private List<Integer> circuitBandwidths;
    
    //nº total de circuitos
    private int totalCircuits;
    //cria novas collections
    public Metrics() {
        this.globalNodes = new HashSet<>();
        this.guardNodes = new HashSet<>();
        this.middleNodes = new HashSet<>();
        this.exitNodes = new HashSet<>();
        
        this.globalNodeFrequency = new HashMap<>();
        this.guardNodeFrequency = new HashMap<>();
        this.middleNodeFrequency = new HashMap<>();
        this.exitNodeFrequency = new HashMap<>();
        
        this.globalCountryFrequency = new HashMap<>();
        this.guardCountryFrequency = new HashMap<>();
        this.middleCountryFrequency = new HashMap<>();
        this.exitCountryFrequency = new HashMap<>();
        
        this.circuitBandwidths = new ArrayList<>();
        this.totalCircuits = 0;
    }
    
    //add dos dados do circuito ao conjunto de metricas ignorando circuitos invalidos 
    public void addCircuit(Circuit circuit) {
        if (circuit == null || circuit.getNodes() == null || circuit.getNodes().length != 3) {
            return;
        }
        
        Node[] nodes = circuit.getNodes();
        Node guard = nodes[0];
        Node middle = nodes[1];
        Node exit = nodes[2];
        
        //add nodes aos conjuntos
        globalNodes.add(guard.getFingerprint());
        globalNodes.add(middle.getFingerprint());
        globalNodes.add(exit.getFingerprint());
        
        guardNodes.add(guard.getFingerprint());
        middleNodes.add(middle.getFingerprint());
        exitNodes.add(exit.getFingerprint());
        
        //atualiza a sua frequencia
        updateFrequency(globalNodeFrequency, guard.getFingerprint());
        updateFrequency(globalNodeFrequency, middle.getFingerprint());
        updateFrequency(globalNodeFrequency, exit.getFingerprint());
        
        updateFrequency(guardNodeFrequency, guard.getFingerprint());
        updateFrequency(middleNodeFrequency, middle.getFingerprint());
        updateFrequency(exitNodeFrequency, exit.getFingerprint());
        
        //atualiza a frequencia dos paises
        updateFrequency(globalCountryFrequency, guard.getCountry());
        updateFrequency(globalCountryFrequency, middle.getCountry());
        updateFrequency(globalCountryFrequency, exit.getCountry());
        
        updateFrequency(guardCountryFrequency, guard.getCountry());
        updateFrequency(middleCountryFrequency, middle.getCountry());
        updateFrequency(exitCountryFrequency, exit.getCountry());
        
        //adiciona bandwidth
        circuitBandwidths.add(circuit.getMinBandwidth());
        //incrementa o contador
        totalCircuits++;
    }
    //incrementação do contar num frequencyMap
    private void updateFrequency(Map<String, Integer> frequencyMap, String key) {
        frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1); //retorna 0 caso a key não existir
    }
    
    //cardinalidade do conjunto de nodes globais
    public int getGlobalNodeDiversity() {
        return globalNodes.size();
    }
    
    //cardinalidade de guard nodes
    public int getGuardNodeDiversity() {
        return guardNodes.size();
    }
    
    //cardinalidade dos middles nodes
    public int getMiddleNodeDiversity() {
        return middleNodes.size();
    }
    
    //cardinalidade dos exit nodes
    public int getExitNodeDiversity() {
        return exitNodes.size();
    }
    
    //calcular a entropia de Shannon para nodes globais
    public double calculateGlobalNodeEntropy() {
        if (totalCircuits == 0) return 0.0;
        
        int totalNodesSelected = 3 * totalCircuits; // 3 nós por circuito
        double entropy = 0.0;
        for (Integer frequency : globalNodeFrequency.values()) {
            double probability = (double) frequency / totalNodesSelected; //p(xi)= ni/nt
            if (probability > 0) {
                entropy -= probability * (Math.log(probability) / Math.log(2.0));
            }
        }
        return entropy;
    }
    
    //calcular a entropia de Shannon para guard nodes
    public double calculateGuardNodeEntropy() {
        if (totalCircuits == 0) return 0.0;
        
        double entropy = 0.0;
        for (Integer frequency : guardNodeFrequency.values()) {
            double probability = (double) frequency / totalCircuits;//p(xi)=ni/m
            if (probability > 0) {
                entropy -= probability * (Math.log(probability) / Math.log(2.0));
            }
        }
        return entropy;
    }
    
    //calcular a entropia de Shannon para middle nodes
    public double calculateMiddleNodeEntropy() {
        if (totalCircuits == 0) return 0.0;
        
        double entropy = 0.0;
        for (Integer frequency : middleNodeFrequency.values()) {
            double probability = (double) frequency / totalCircuits;//p(xi)=ni/m
            if (probability > 0) {
                entropy -= probability * (Math.log(probability) / Math.log(2.0));
            }
        }
        return entropy;
    }
    
    //calcular a entropia de Shannon para exit nodes
    public double calculateExitNodeEntropy() {
        if (totalCircuits == 0) return 0.0;
        
        double entropy = 0.0;
        for (Integer frequency : exitNodeFrequency.values()) {
            double probability = (double) frequency / totalCircuits;//p(xi)=ni/m
            if (probability > 0) {
                entropy -= probability * (Math.log(probability) / Math.log(2.0));
            }
        }
        
        return entropy;
    }
    
    //calcular a entropia de Shannon para os paises
    public double calculateGlobalCountryEntropy() {
        if (totalCircuits == 0) return 0.0;
        
        int totalCountriesSelected = 3 * totalCircuits;
        double entropy = 0.0;
        for (Integer frequency : globalCountryFrequency.values()) {
            double probability = (double) frequency / totalCountriesSelected;//p(xi)=ni/nt
            if (probability > 0) {
                entropy -= probability * (Math.log(probability) / Math.log(2.0));
            }
        }
        return entropy;
    }
    
    //calcular a entropia de Shannon para os paises-guard
    public double calculateGuardCountryEntropy() {
        if (totalCircuits == 0) return 0.0;
        
        double entropy = 0.0;
        for (Integer frequency : guardCountryFrequency.values()) {
            double probability = (double) frequency / totalCircuits;//p(xi)=ni/m
            if (probability > 0) {
                entropy -= probability * (Math.log(probability) / Math.log(2.0));
            }
        }
        return entropy;
    }
    
    //calcular a entropia de Shannon para os paises-middle
    public double calculateMiddleCountryEntropy() {
        if (totalCircuits == 0) return 0.0;
        
        double entropy = 0.0;
        for (Integer frequency : middleCountryFrequency.values()) {
            double probability = (double) frequency / totalCircuits;//p(xi)=ni/m
            if (probability > 0) {
                entropy -= probability * (Math.log(probability) / Math.log(2.0));
            }
        }
        return entropy;
    }
    
    //calcular a entropia de Shannon para os paises-exit
    public double calculateExitCountryEntropy() {
        if (totalCircuits == 0) return 0.0;
        
        double entropy = 0.0;
        
        for (Integer frequency : exitCountryFrequency.values()) {
            double probability = (double) frequency / totalCircuits;//p(xi)=ni/m
            if (probability > 0) {
                entropy -= probability * (Math.log(probability) / Math.log(2.0));
            }
        }
        return entropy;
    }
    
    //estatisticas da bandwidth
    public BandwidthStats getBandwidthStats() {
        if (circuitBandwidths.isEmpty()) {
            return new BandwidthStats(0, 0, 0, 0);
        }
        int min = Collections.min(circuitBandwidths);
        int max = Collections.max(circuitBandwidths);
        double avg = circuitBandwidths.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double median = calculateMedian(circuitBandwidths);
        
        return new BandwidthStats(min, max, avg, median);
    }
    //calcular a mediana da lista de inteiros
    private double calculateMedian(List<Integer> values) {
        List<Integer> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 0) {
            //se for par a média é dos dois valores centrais
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        } else {
            //se for impar é do valor central
            return sorted.get(size / 2);
        }
    }
    
    //nº total de circuitos que foram processados
    public int getTotalCircuits() {
        return totalCircuits;
    }
    
    //guardar resultados da estatica da bandwidth
    public static class BandwidthStats {
        public final int min;
        public final int max;
        public final double average;
        public final double median;
        
    public BandwidthStats(int min, int max, double average, double median) {
            this.min = min;
            this.max = max;
            this.average = average;
            this.median = median;
        }
    }
}

