package ejemplos;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;

@InitiatingFlow // Flujos que empiezan en un nodo
@StartableByRPC // Puede ser invocado por el nodo que opera la blockchain
//@InitiatedBy() // Flujo iniciado por otro nodo
public class VerySimpleFlow extends FlowLogic<Boolean> {

    @Suspendable
    public Boolean call() throws FlowException {
        return true;
    }
}
