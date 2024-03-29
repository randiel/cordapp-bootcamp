package ejemplos;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContainerState implements ContractState {
    private int width;
    private int height;
    private int depth;

    private String contents;

    private Party owner;
    private Party carrier;

    public ContainerState(int width, int height, int depth, String contents, Party owner, Party carrier) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.contents = contents;
        this.owner = owner;
        this.carrier = carrier;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public String getContents() {
        return contents;
    }

    public Party getOwner() {
        return owner;
    }

    public Party getCarrier() {
        return carrier;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        // Uno establecer quienes deben ser notificados al actualizar este estado en el ledger
        return ImmutableList.of(owner, carrier);
    }

    public static void main ( String[] args) {
        Party jetpackImporters = null;
        Party jetpackCarriers = null;
        ContainerState container = new ContainerState(
                2,
                3,
                4,
                "Jetpacks",
                jetpackImporters,
                jetpackCarriers
        );
    }

}
