package ejemplos;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.ServiceHub;

import java.util.List;

@InitiatedBy(TwoPartyFlow.class)
public class TwoPartyFlowResponder extends FlowLogic<Void> {
    private FlowSession counterpartySession;

    public TwoPartyFlowResponder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    public Void call() throws FlowException {

        ServiceHub serviceHub = getServiceHub();
        List<StateAndRef<HouseState>> statesFromVault = serviceHub.getVaultService().queryBy(HouseState.class).getStates();

        CordaX500Name aliceName = new CordaX500Name("Alice","Trujillo", "PE");
        NodeInfo alice = serviceHub.getNetworkMapCache().getNodeByLegalName(aliceName);

        int platformVersion = serviceHub.getMyInfo().getPlatformVersion();

        // Este es un ejemplo trivial de recepcion de la información de la contraparte
        // pero en el caso que querramos recibir una transacción firmada por otros participantes
        // va generandose más complejidad.
        int receivedInt = counterpartySession.receive(Integer.class).unwrap(it -> {
            if (it>3) {
                throw new IllegalArgumentException("Número muy alto");
            }
            return it;
        });

        int receivedIntPlusOne = receivedInt + 1;
        counterpartySession.send(receivedIntPlusOne);

        return null;
    }
}
