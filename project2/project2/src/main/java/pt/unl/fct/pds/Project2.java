package pt.unl.fct.pds;

import pt.unl.fct.pds.project2.model.Node;
import pt.unl.fct.pds.project2.model.Circuit;
import pt.unl.fct.pds.project2.utils.ConsensusParser;
import pt.unl.fct.pds.project2.utils.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Project2 
{
    private static final Random rand = new Random();
    private static final String CONSENSUS_FILENAME = "consensus.txt";
    //algorimo 2
    private static final double ALPHA = 0.8; 
    private static final double BETA = 0.2;
    
    //nº de circuitos
    private static final int NUM_SIMULATIONS = 1000; 

    //main 
    public static void main( String[] args )
    {
        System.out.println("==========================\n");
        System.out.println("Welcome to your Simulator");
        System.out.println("==========================\n");

        //parse 
        ConsensusParser parser = new ConsensusParser(CONSENSUS_FILENAME);
        Node[] allNodes = parser.parseConsensus();

        if (allNodes == null || allNodes.length == 0) {
            System.err.println("Failt to load the nodes");
            return;
        }
        System.out.println("Loaded " + allNodes.length + " nodes\n");
        
        List<Node> nodeList = new ArrayList<>(Arrays.asList(allNodes));

        //algoritmo1
        System.out.println("=== Executing Algoritm 1 ===");
        System.out.println("Number of circuits: " + NUM_SIMULATIONS + "\n");
        Metrics metricsAlgo1 = runSimulation(nodeList, 1, 0, 0);
        
        //algoritmo2
        System.out.println("\n=== Executing Algoritm 2 ===");
        System.out.println("Number of circuits: " + NUM_SIMULATIONS);
        System.out.println("Parameters: Alpha=" + ALPHA + ", Beta=" + BETA + "\n");
        Metrics metricsAlgo2 = runSimulation(nodeList, 2, ALPHA, BETA);
        
        //comparação entre ambos
        System.out.println("=== RELATÓRIO DE COMPARAÇÃO ===");
        generateComparisonReport(metricsAlgo1, metricsAlgo2);
    }
    
    //simulacao de multiplos circuitos
    private static Metrics runSimulation(List<Node> nodeList, int algorithm, double alpha, double beta) {
        Metrics metrics = new Metrics();
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < NUM_SIMULATIONS; i++) {
            Circuit circuit = null;
            if (algorithm == 1) {
                circuit = selectPathAlgorithm1(nodeList);
            } else if (algorithm == 2) {
                circuit = selectPathAlgorithm2(nodeList, alpha, beta);
            }
            if (circuit != null) {
                metrics.addCircuit(circuit);
                successCount++;
            } else {
                failCount++;
            }
            
            //indica o progresso feito
            if ((i + 1) % 100 == 0) {
                System.out.print("Progress: " + (i + 1) + "/" + NUM_SIMULATIONS + " circuits created\r");
            }
        }
        
        System.out.println("\nSimulation completed with SUCCESS!");
        System.out.println("Circuits created successfully: " + successCount);
        System.out.println("Failed circuits: " + failCount);
        System.out.println("Success rate: " + String.format("%.2f", (successCount * 100.0 / NUM_SIMULATIONS)) + "%\n");
        return metrics;
    }
    
    //cria um relatorio com as metricas 
    private static void generateComparisonReport(Metrics metricsAlgo1, Metrics metricsAlgo2) {
        System.out.println("NODE DIVERSITY METRICS");
        System.out.println("Algoritm 1:");
        System.out.println(" Global Diversity: " + metricsAlgo1.getGlobalNodeDiversity());
        System.out.println(" Guards Diversity: " + metricsAlgo1.getGuardNodeDiversity());
        System.out.println(" Middle Diversity: " + metricsAlgo1.getMiddleNodeDiversity());
        System.out.println(" Exit Diversit: " + metricsAlgo1.getExitNodeDiversity());
        
        System.out.println("\nAlgoritm 2:");
        System.out.println(" Global Diversity: " + metricsAlgo2.getGlobalNodeDiversity());
        System.out.println(" Guards Diversity: " + metricsAlgo2.getGuardNodeDiversity());
        System.out.println(" Middle Diversity: " + metricsAlgo2.getMiddleNodeDiversity());
        System.out.println(" Exit Diversity: " + metricsAlgo2.getExitNodeDiversity());
        
        System.out.println("\nSHANNON ENTROPY - Nodes ");
        System.out.println("Algoritm 1:");
        System.out.println(" Global Entropy: " + String.format("%.3f", metricsAlgo1.calculateGlobalNodeEntropy()));
        System.out.println(" Guards Entropy: " + String.format("%.3f", metricsAlgo1.calculateGuardNodeEntropy()));
        System.out.println(" Middle Entropy: " + String.format("%.3f", metricsAlgo1.calculateMiddleNodeEntropy()));
        System.out.println(" Exit Entropy: " + String.format("%.3f", metricsAlgo1.calculateExitNodeEntropy()));
        
        System.out.println("\nAlgoritmo 2:");
        System.out.println(" Global Entropy: " + String.format("%.3f", metricsAlgo2.calculateGlobalNodeEntropy()));
        System.out.println(" Guards Entropy: " + String.format("%.3f", metricsAlgo2.calculateGuardNodeEntropy()));
        System.out.println(" Middle Entropy: " + String.format("%.3f", metricsAlgo2.calculateMiddleNodeEntropy()));
        System.out.println(" Exit Entropy: " + String.format("%.3f", metricsAlgo2.calculateExitNodeEntropy()));
        
        System.out.println("\nSHANNON ENTROPY - Country");
        System.out.println("Algoritm 1:");
        System.out.println(" Global Entropy: " + String.format("%.3f", metricsAlgo1.calculateGlobalCountryEntropy()));
        System.out.println(" Guards Entropy: " + String.format("%.3f", metricsAlgo1.calculateGuardCountryEntropy()));
        System.out.println(" Middle Entropy: " + String.format("%.3f", metricsAlgo1.calculateMiddleCountryEntropy()));
        System.out.println(" Exit Entropy: " + String.format("%.3f", metricsAlgo1.calculateExitCountryEntropy()));
        
        System.out.println("\nAlgoritm 2:");
        System.out.println(" Global Entropy: " + String.format("%.3f", metricsAlgo2.calculateGlobalCountryEntropy()));
        System.out.println(" Guards Entropy: " + String.format("%.3f", metricsAlgo2.calculateGuardCountryEntropy()));
        System.out.println(" Middle Entropy: " + String.format("%.3f", metricsAlgo2.calculateMiddleCountryEntropy()));
        System.out.println(" Exit Entropy: " + String.format("%.3f", metricsAlgo2.calculateExitCountryEntropy()));
        
        System.out.println("\nBANDWIDTH STATISTICS");
        Metrics.BandwidthStats stats1 = metricsAlgo1.getBandwidthStats();
        Metrics.BandwidthStats stats2 = metricsAlgo2.getBandwidthStats();
        
        System.out.println("Algoritm 1:");
        System.out.println("  Min: " + stats1.min);
        System.out.println("  Max: " + stats1.max);
        System.out.println("  Average: " + String.format("%.2f", stats1.average));
        System.out.println("  Median: " + String.format("%.2f", stats1.median));
        
        System.out.println("\nAlgoritm 2:");
        System.out.println("  Min: " + stats2.min);
        System.out.println("  Max: " + stats2.max);
        System.out.println("  Average: " + String.format("%.2f", stats2.average));
        System.out.println("  Median: " + String.format("%.2f", stats2.median));
        
        System.out.println("\nComparation");
        System.out.println("Improving Global Diversity: " + 
            (metricsAlgo2.getGlobalNodeDiversity() - metricsAlgo1.getGlobalNodeDiversity()) + " nodes: " + String.format("%.2f", ((metricsAlgo2.getGlobalNodeDiversity() - metricsAlgo1.getGlobalNodeDiversity()) * 100.0 / Math.max(metricsAlgo1.getGlobalNodeDiversity(), 1))) + "%");
        System.out.println("Improvement in Global Entropy- Nodes : " + 
            String.format("%.3f", (metricsAlgo2.calculateGlobalNodeEntropy() - metricsAlgo1.calculateGlobalNodeEntropy())));
        System.out.println("Improvement in Global Entropy- Country: " + 
            String.format("%.3f", (metricsAlgo2.calculateGlobalCountryEntropy() - metricsAlgo1.calculateGlobalCountryEntropy())));
        System.out.println("Difference in Average Bandwidth: " + 
            String.format("%.2f", (stats2.average - stats1.average)) + " (" + 
            String.format("%.2f", ((stats2.average - stats1.average) * 100.0 / Math.max(stats1.average, 1))) + "%)");
        System.out.println("\n===========END=============");
    }
    //algoritmo1
    public static Circuit selectPathAlgorithm1(List<Node> allNodes) {
        Node exit = selectExit(allNodes); //escolhe o exit node
        if (exit == null) {
             System.err.println("A suitable Exit node could not be found :(");
            return null;
        }

        //escolhe o guard node 
        List<Node> guardCandidates = allNodes.stream()
            .filter(n -> !n.getFingerprint().equals(exit.getFingerprint()))
            //restrições
            .filter(n -> !sharesFamily(n, exit))
            .filter(n -> !sharesSubnet16(n, exit))
            .collect(Collectors.toList());
        Node guard = selectGuard(guardCandidates, exit);
        if (guard == null) {
            System.err.println("A suitable Guard node could not be found :(");
            return null;
        }

        //escolhe o middle node
        List<Node> middleCandidates = allNodes.stream()
            .filter(n -> !n.getFingerprint().equals(exit.getFingerprint()) && !n.getFingerprint().equals(guard.getFingerprint()))
            //restrições
            .filter(n -> !sharesFamily(n, exit))
            .filter(n -> !sharesFamily(n, guard))
            .filter(n -> !sharesSubnet16(n, exit))
            .filter(n -> !sharesSubnet16(n, guard)) 
            .collect(Collectors.toList());
        Node middle = selectMiddle(middleCandidates, guard, exit);
        if (middle == null) {
            System.err.println("A suitable Middle node could not be found :(");
            return null;
        }
        //construção do circuito
        Node[] circuitNodes = new Node[]{guard, middle, exit};
        int minBandwidth = Arrays.stream(circuitNodes).mapToInt(Node::getBandwidth).min().orElse(0);
        return new Circuit(1, circuitNodes, minBandwidth);
    }

    //escolhe exit node e filtra por flag Fast e Exit
    private static Node selectExit(List<Node> allNodes) {
        List<Node> candidates = allNodes.stream()
            .filter(n -> hasFlag(n, "Fast"))
            .filter(n -> isSuitableExit(n))
            .collect(Collectors.toList());
        return selectNodeWeightedByBandwidth(candidates);
    }
    //escolhe um guard node e filtra por flag Fast
    private static Node selectGuard(List<Node> candidates, Node exit) {
        List<Node> guardCandidates = candidates.stream()
            .filter(n -> hasFlag(n, "Guard"))
            .collect(Collectors.toList());
        return selectNodeWeightedByBandwidth(guardCandidates);
    }
    //escolhe um middle node e filtra por fast 
    private static Node selectMiddle(List<Node> candidates, Node guard, Node exit) {
        List<Node> middleCandidates = candidates.stream()
            .filter(n -> hasFlag(n, "Fast"))
            //um middle node não poderá ser um guard 
            .filter(n -> !hasFlag(n, "Guard"))
            .collect(Collectors.toList());
        return selectNodeWeightedByBandwidth(middleCandidates);
    }

    //algoritmo2
    public static Circuit selectPathAlgorithm2(List<Node> allNodes, double alpha, double beta) {
        Node exit = selectExit(allNodes);//escolhe um exit node
        if (exit == null) {
             System.err.println("A suitable Exit node could not be found :(");
            return null;
        }

        //escolhe um guard node
        List<Node> guardCandidates = allNodes.stream()
            .filter(n -> !n.getFingerprint().equals(exit.getFingerprint()))
            //restrições
            .filter(n -> !sharesFamily(n, exit))
            .filter(n -> !sharesSubnet16(n, exit))
            .collect(Collectors.toList());
        Node guard = selectGuardGeo(guardCandidates, exit, alpha); 
        if (guard == null) {
            System.err.println("A suitable Guard node could not be found :(");
            return null;
        }

        //escolhe um middle node
        List<Node> middleCandidates = allNodes.stream()
            .filter(n -> !n.getFingerprint().equals(exit.getFingerprint()) && n.getFingerprint().equals(guard.getFingerprint()))
            //restrições
            .filter(n -> !sharesFamily(n, exit))
            .filter(n -> !sharesFamily(n, guard))
            .filter(n -> !sharesSubnet16(n, exit))
            .filter(n -> !sharesSubnet16(n, guard))
            .collect(Collectors.toList());
        Node middle = selectMiddleGeo(middleCandidates, guard, exit, beta);
        if (middle == null) {
            System.err.println("A suitable Middle node could not be found :(");
            return null;
        }
        //construção do circuito
        Node[] circuitNodes = new Node[]{guard, middle, exit};
        int minBandwidth = Arrays.stream(circuitNodes).mapToInt(Node::getBandwidth).min().orElse(0);
        return new Circuit(2, circuitNodes, minBandwidth);
    }

    //escolhe um guard node com geo - alpha
    private static Node selectGuardGeo(List<Node> candidates, Node exit, double alpha) {
        List<Node> guardCandidates = candidates.stream() //filtra pela guard flag
            .filter(n -> hasFlag(n, "Guard"))
            .collect(Collectors.toList());

        List<Double> geoWeights = new ArrayList<>();
        String exitCountry = exit.getCountry();

        for (Node node : guardCandidates) {
            double weight = node.getBandwidth();
            //caso o country for diferente este multiplica
            if (!node.getCountry().equals(exitCountry)) {
                weight = weight * (1.0 + alpha);
            }
            geoWeights.add(weight);
        }
        return selectNodeWeighted(guardCandidates, geoWeights);
    }
    //escolhe um middle node com geo - beta
    private static Node selectMiddleGeo(List<Node> candidates, Node guard, Node exit, double beta) {
        List<Node> middleCandidates = candidates.stream() //filtra a fast flag e exclui as guard
            .filter(n -> hasFlag(n, "Fast"))
            .filter(n -> !hasFlag(n, "Guard"))
            .collect(Collectors.toList());

        List<Double> geoWeights = new ArrayList<>();
        String exitCountry = exit.getCountry();
        String guardCountry = guard.getCountry();

        for (Node node : middleCandidates) {
            double weight = node.getBandwidth();
            String nodeCountry = node.getCountry();
            
            int c = 1; 
            //pais diferente em ambos
            if 
                (!nodeCountry.equals(guardCountry) && !nodeCountry.equals(exitCountry)) c = 3;
            //pais diferente so de um deles
            else if
                (!nodeCountry.equals(guardCountry) || !nodeCountry.equals(exitCountry)) c = 2;
            else 
                c = 1; //igual a ambos
            weight = weight * (1.0 + (beta * c));
            geoWeights.add(weight);
        }
        return selectNodeWeighted(middleCandidates, geoWeights);
    }
    //verifica se 2 nodes partilham a mesma Family
    private static boolean sharesFamily(Node nodeA, Node nodeB) {
        //se não tiver family assume que não partilham 
        if (nodeA.getFamily() == null || nodeB.getFamily() == null ||nodeA.getFamily().length == 0 || nodeB.getFamily().length == 0) {
            return false;
        }
        //arrays para set para uma procura mais rápida
        Set<String> familyA = new HashSet<>(Arrays.asList(nodeA.getFamily()));
        
        //verificar se algum membro de B está em A
        for (String familyMember : nodeB.getFamily()) {
            if (familyA.contains(familyMember)) {
                return true;//tem em comum
            }
        }
        return false;//não tem 
    }

    //verifica se 2 nodes partilham a sub-rede/16
    private static boolean sharesSubnet16(Node nodeA, Node nodeB) {
        //obtem os octetos do ip
        String[] ipA = nodeA.getIpAddress().split("\\.");
        String[] ipB = nodeB.getIpAddress().split("\\.");

        if (ipA.length < 2 || ipB.length < 2) {
            return false;
        }
        //compara 2 octetos
        return ipA[0].equals(ipB[0]) && ipA[1].equals(ipB[1]);
    }
    //escolhe um node pela bandwidth
    private static Node selectNodeWeightedByBandwidth(List<Node> candidates) {
        if (candidates == null || candidates.isEmpty()) return null;
        //converte integers para doubles 
        List<Double> weights = candidates.stream()
            .map(n -> (double) n.getBandwidth())
            .collect(Collectors.toList());
        return selectNodeWeighted(candidates, weights);
    }
    //escolhe um node usando doubles
    private static Node selectNodeWeighted(List<Node> candidates, List<Double> weights) {
        if (candidates == null || candidates.isEmpty() || candidates.size() != weights.size()) return null;
        //peso total ; se forem todos 0 escolhe aleatoriamente
        double totalWeight = weights.stream().mapToDouble(Double::doubleValue).sum();
        if (totalWeight == 0) return candidates.get(rand.nextInt(candidates.size()));
        //gera um nº aleatorio entre o 0 e Wt
        double randomWeight = rand.nextDouble() * totalWeight;
        //refaz até encontrar um node
        double cumulativeWeight = 0;
        for (int i = 0; i < candidates.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (randomWeight < cumulativeWeight) {
                return candidates.get(i);
            }
        }
        return candidates.get(candidates.size() - 1);
    }
    //ve se um node tem um flag
    private static boolean hasFlag(Node node, String flag) {
        if (node.getFlags() == null) return false;
        for (String f : node.getFlags()) {
            if (f.equalsIgnoreCase(flag)) return true;
        }
        return false;
    }
    //ve se um node tem exit
    private static boolean isSuitableExit(Node node) {
        return hasFlag(node, "Exit");
    }
}