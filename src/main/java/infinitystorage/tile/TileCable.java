package infinitystorage.tile;

import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.InfinityStorageItems;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TileCable extends TileMultipartNode {

    /**
     * A list of machines adjacent the cable
     */
    private List<TileNode> machinesAround = new ArrayList<>();

    /**
     * The distance of the cable from the controller
     */
    public int cableNumber;

    /**
     * A list of the cables connected to the current cable
     */
    private List<TileCable> childCables = new ArrayList<>();

    public TileCable(){
        super();
        setupClientNode(new ItemStack(InfinityStorageBlocks.CABLE), InfinityStorage.INSTANCE.cableUsage);
    }

    @Override
    public int getEnergyUsage() {
        return InfinityStorage.INSTANCE.cableUsage;
    }

    @Override
    public void updateNode() {
        // NO OP
    }

    /**
     * Gets a list of machines around the cable, not including other cables
     * @return A list of machines around the cable
     */
    public List<TileNode> getNodesInArea(){
        return machinesAround;
    }

    /**
     * Add or remove a node from the list of machines or cables adjacent to the current cable
     * @param node The node to add or remove
     * @param aor Add or remove
     */
    public void changeNodeConnection(TileNode node, boolean aor){
        if(aor){
            if(node instanceof TileCable){
                TileCable c = (TileCable) node;
                childCables.add(c);
                c.setCableNumber(cableNumber + 1);
            }else{
                machinesAround.add(node);
            }
        }else{
            if(node instanceof TileCable){
                childCables.remove(childCables.indexOf(node));
            }else{
                machinesAround.remove(machinesAround.indexOf(node));
            }
        }
    }

    /**
     * Sets the cableNumber
     * @param number The number to set the cable
     */
    public void setCableNumber(int number){
        cableNumber = number;
    }

    /**
     * Returns whether or not a machine should connect to the network
     * @return Whether or not a machine should connect to the network
     */
    public boolean shouldConnect(){
        return true;
    }
}
