import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {


    private static List<FactoryLink> factoryLinks;
    private static Map<Integer, Factory> factoryMap = new HashMap<>();
    private static Map<Integer, Troop> troopMap = new HashMap<>();


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int factoryCount = in.nextInt(); // the number of factories
        int linkCount = in.nextInt(); // the number of links between factories

        factoryLinks = IntStream.range(0, linkCount)
                .mapToObj(x -> new FactoryLink(in.nextInt(), in.nextInt(), in.nextInt()))
                .collect(toList());

        // game loop
        while (true) {
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            List<Factory> factories = new ArrayList<>();
            List<Troop> troops = new ArrayList<>();

            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();

                if (entityType.equals("FACTORY")) {
                    Factory factory = new Factory(entityId, FactoryOwner.parseCode(in.nextInt()), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), getLinksForFactory(entityId));
                    factories.add(factory);
                    factoryMap.put(factory.id, factory);
                } else {
                    Troop troop = new Troop(entityId, TroopOwner.parseCode(in.nextInt()), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
                    troops.add(troop);
                    troopMap.put(troop.id, troop);

                }

            }


            List<Factory> myFactories = getMyFactories(factories);

            Collections.shuffle(myFactories);
            Factory myFactory = myFactories.get(0);

            Optional<Factory> ftargetFactory = Stream.of(getClosestNeutralFromMy(myFactory), getClosestNeutralFromMy(myFactory))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();

            if (ftargetFactory.isPresent()) {
                ftargetFactory.ifPresent(factory -> myFactory.moveTo(factory.id, myFactory.cyborgsCount));
            } else {

                System.out.println("WAIT");
            }

        }
    }


    //TODO
    static Optional<Factory> getClosestNeutralFromMy(Factory myFactory) {

        return myFactory.factoryLinks.stream()
                .filter(factoryLink -> factoryLink.childId != myFactory.id)
                .min(Comparator.comparing(factoryLink -> factoryLink.distance))
                .map(factoryLink -> factoryMap.get(factoryLink.childId));

    }

    static Optional<Factory> getClosestEnemyFromMy(Factory myFactory) {

        return myFactory.factoryLinks.stream()
                .min(Comparator.comparing(factoryLink -> factoryLink.distance))
                .map(factoryLink -> factoryMap.get(factoryLink.childId));

    }

    static List<Factory> getMyFactories(List<Factory> factories) {
        return factories.stream().filter(factory -> factory.owner == FactoryOwner.PLAYER)
                .collect(toList());
    }

    static List<Factory> getNeutralFactories(List<Factory> factories) {
        return factories.stream().filter(factory -> factory.owner == FactoryOwner.PLAYER)
                .collect(toList());
    }

    static List<Factory> getEnemyFactories(List<Factory> factories) {
        return factories.stream().filter(factory -> factory.owner == FactoryOwner.PLAYER)
                .collect(toList());
    }


    static List<FactoryLink> getLinksForFactory(int id) {
        return factoryLinks.stream()
                .filter(factoryLink -> factoryLink.parentId == id)
                .collect(toList());

    }

    static class Troop {
        private int id;
        private TroopOwner owner;
        private int sourceFactoryId;
        private int targetFactoryId;
        private int cyborgsCount;
        private int turnsToTarget;

        public Troop(int id, TroopOwner owner, int sourceFactoryId, int targetFactoryId, int cyborgsCount, int turnsToTarget) {
            this.id = id;
            this.owner = owner;
            this.sourceFactoryId = sourceFactoryId;
            this.targetFactoryId = targetFactoryId;
            this.cyborgsCount = cyborgsCount;
            this.turnsToTarget = turnsToTarget;
        }
    }

    static class Factory {
        private final int id;
        private FactoryOwner owner;
        private int cyborgsCount;
        private int factoryProduction;
        private List<FactoryLink> factoryLinks;

        public Factory(int id, FactoryOwner owner, int cyborgsCount, int factoryProduction, int unused1, int unused2, List<FactoryLink> factoryLinks) {
            this.id = id;
            this.owner = owner;
            this.cyborgsCount = cyborgsCount;
            this.factoryProduction = factoryProduction;
            this.factoryLinks = factoryLinks;
        }


        public void moveTo(int destination, int count) {
            System.out.println("MOVE " + id + " " + destination + " " + count);
        }
    }

    static class FactoryLink {

        private final int parentId;
        private final int childId;
        private final int distance;


        FactoryLink(int parentId, int childId, int distance) {
            this.parentId = parentId;
            this.childId = childId;
            this.distance = distance;
        }
    }


    enum TroopOwner {
        PLAYER(1), ENEMY(-1);
        private final int code;

        TroopOwner(int i) {
            this.code = i;
        }

        public static TroopOwner parseCode(int i) {
            return Arrays.stream(TroopOwner.values())
                    .filter(owner -> owner.code == i)
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
        }

    }

    enum FactoryOwner {
        PLAYER(1), NEUTRAL(0), ENEMY(-1);
        private final int code;


        FactoryOwner(int i) {
            this.code = i;
        }

        public static FactoryOwner parseCode(int i) {
            return Arrays.stream(FactoryOwner.values())
                    .filter(owner -> owner.code == i)
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
        }

    }

}