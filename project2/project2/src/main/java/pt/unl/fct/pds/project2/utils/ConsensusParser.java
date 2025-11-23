package pt.unl.fct.pds.project2.utils;

import pt.unl.fct.pds.project2.model.Node;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import do geoip
import java.io.File;
import java.net.InetAddress;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;

//import do metric
// import org.torproject.metrics.descriptor.DescriptorSource;
// import org.torproject.metrics.descriptor.DescriptorSources;
// import org.torproject.metrics.descriptor.RelayDescriptor;


public class ConsensusParser {
    String filename;
    //ler a BD do geoip
    private DatabaseReader dbReader;


    //metrics-lib
    // private DescriptorSource descriptorSource;

    public ConsensusParser(String filename) {
        this.filename = filename;
        
        //inicia o geoip
        try {
            File database = new File("GeoLite2-Country.mmdb");
            dbReader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            System.err.println("ERROR: Could not load the GeoIP database!");
            dbReader = null;
        }

        //iniciar os descritores
        System.out.println("metrics lib unavailable");
    }

    //geolocaliza um Ip
    private String geoLocateIp(String ipAddress) {
        if (dbReader == null) return "Unknown (DB Error)";
        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            CountryResponse response = dbReader.country(ip);
            String countryIso = response.getCountry().getIsoCode();
            return (countryIso != null) ? countryIso : "Unknown";
        } catch (IOException | GeoIp2Exception e) {
            return "Unknown";
        }
    }
    //nova node family 
    private String[] getNodeFamily(String fingerprint) {
        return new String[0];
    }


    //le o consenso e vai retornar o array dos nodes
    public Node[] parseConsensus() {
        if (dbReader == null) {
            System.err.println("Parser cannot continue, GeoIP failed to initialize");
            return new Node[0]; 
        }

        List<Node> nodes = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int nodeCounter = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            Node currentNode = null;

            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(" ");
                if (parts.length == 0) continue; 

                switch (parts[0]) {
                    //começa um novo node
                    case "r":
                        if (currentNode != null) {
                            nodes.add(currentNode);
                        }
                        nodeCounter++;
                        System.out.println("A processar nó " + nodeCounter + "...");
                        //vai criar e preencher cada node 
                        currentNode = new Node();
                        String nickname = parts[1];
                        String fingerprint = parts[2];
                        
                        currentNode.setNickname(nickname);
                        currentNode.setFingerprint(fingerprint);
                        String publicationTimeStr = parts[4] + " " + parts[5];
                        currentNode.setTimePublished(LocalDateTime.parse(publicationTimeStr, formatter));
                        String ip = parts[6];
                        currentNode.setIpAddress(ip);
                        currentNode.setOrPort(Integer.parseInt(parts[7]));
                        currentNode.setDirPort(Integer.parseInt(parts[8]));

                        //geolocaliza o Ip
                        currentNode.setCountry(geoLocateIp(ip));
                        
                        //obtem a family
                        currentNode.setFamily(getNodeFamily(fingerprint));
                        break;
                    //flag do node
                    case "s":
                        if (currentNode != null) {
                            currentNode.setFlags(Arrays.copyOfRange(parts, 1, parts.length));
                        }
                        break;
                    //versão do node
                    case "v":
                        if (currentNode != null) currentNode.setVersion(parts[1]);
                        break;
                    case "pr":
                        break;
                    //bandwidth
                    case "w":
                        if (currentNode != null && parts.length > 1 && parts[1].startsWith("Bandwidth=")) {
                            currentNode.setBandwidth(Integer.parseInt(parts[1].split("=")[1]));
                        }
                        break;
                    //exit policy
                    case "p":
                        if (currentNode != null && parts.length > 1) {
                            currentNode.setExitPolicy(parts[1]);
                        }
                        break;
                    default:
                        //irá acabar o node caso estivesse a ser feito
                        if (currentNode != null) {
                            nodes.add(currentNode);
                            currentNode = null;
                        }
                        break;
                }
            }
            //vai adicionar o ultimo node que estava a ser processado
            if (currentNode != null) {
                nodes.add(currentNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //fecha os readers
        try {
            dbReader.close();
        } catch (IOException e) { e.printStackTrace(); }

        System.out.println("Processing complete. Total of " + nodes.size() + " loaded nodes ");
        return nodes.toArray(new Node[0]);
    }
}