package bootcamp;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {

    public static String ID = "bootcamp.TokenContract";

    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {

        // 1. Restricciones de forma, # de inputs, # de outputs, # de comandos
        if (tx.getInputStates().size()!=0) {
            throw new IllegalArgumentException("La transacción no debe tener entradas");
        }
        if (tx.getOutputStates().size()!=1) {
            throw new IllegalArgumentException("La transacción debe tener solo una salida");
        }
        if (tx.getCommands().size()!=1) {
            throw new IllegalArgumentException("La transacción solo debe tener un comando");
        }

        // 2. Restricciones de contenido
        ContractState output = tx.getOutput(0);
        Command command = tx.getCommand(0);

        if (!(output instanceof TokenState)) {
            throw new IllegalArgumentException("La salida debe ser del tipo TokenState");
        }
        if (!(command.getValue() instanceof TokenContract.Commands.Issue)) {
            throw new IllegalArgumentException("El comando debe ser: Issue");
        }

        TokenState token = (TokenState) output;
        if (token.getAmount() <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }

        // 3. Firmantes requeridos
        List<PublicKey> requiredSigners = command.getSigners();
        Party issuer = token.getIssuer();
        PublicKey issuersKey = issuer.getOwningKey();
        if (!(requiredSigners.contains(issuersKey))) {
            throw new IllegalArgumentException("El comando requiere estar firmado por el emisor.");
        }


    }


}