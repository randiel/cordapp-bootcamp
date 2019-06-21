package ejemplos;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

public class HouseContract implements Contract {

    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        if (tx.getCommands().size()!=1) {
            throw new IllegalArgumentException("La transacción debe tener un solo comando");
        }
        Command command = tx.getCommand(0);
        List<PublicKey> requiredSigners = command.getSigners();
        CommandData commandType = command.getValue();

        if (commandType instanceof Register) {

            // Acerca del diseño de las reglas de un contrato, podemos clasificarlas en 3
            // 1. Restricciones de forma, # de inputs, # de outputs, # de comandos
            if (tx.getInputStates().size()!=0) {
                throw new IllegalArgumentException("El comando de Registro no debe tener parámetros de entrada");
            }

            if (tx.getOutputStates().size()!=1) {
                throw new IllegalArgumentException("El comando de Registro solo debe generar una salida");
            }

            // 2. Restricciones de contenido
            ContractState outputState = tx.getOutput(0);
            if (!(outputState instanceof HouseState)) {
                throw new IllegalArgumentException("El estado de salida debe ser del tipo HouseState");
            }

            HouseState houseState = (HouseState) outputState;
            if (houseState.getAddress().length() <= 3) {
                throw new IllegalArgumentException("La dirección de la propiedad debe ser mayor a 3 caracteres");
            }
            if (!(houseState.getOwner().getName().getCountry().equals("Perú"))) {
                throw new IllegalArgumentException("No esta permitido registrar propiedades fuera del territorio peruano");
            }

            // 3. Firmantes requeridos
            Party owner = houseState.getOwner();
            PublicKey ownersKey = owner.getOwningKey();
            if (!(requiredSigners.contains(ownersKey))) {
                throw new IllegalArgumentException("El propietario debe firmar el registro de la propiedad");
            }

        } else if (commandType instanceof Transfer) {
            // 1. Restricciones de forma, # de inputs, # de outputs, # de comandos
            if (tx.getInputStates().size()!=1) {
                throw new IllegalArgumentException("El comando de Transferencia debe tener al menos una entrada");
            }

            if (tx.getOutputStates().size()!=1) {
                throw new IllegalArgumentException("El comando de Transferencia debe tener al menos una salida");
            }

            // 2. Restricciones de contenido
            ContractState input = tx.getInput(0);
            ContractState output = tx.getOutput(0);

            if (!(input instanceof HouseState)) {
                throw new IllegalArgumentException("La Entrada debe ser del tipo HouseState");
            }
            if (!(output instanceof HouseState)) {
                throw new IllegalArgumentException("La Salida debe ser del tipo HouseState");
            }

            HouseState inputHouse = (HouseState)input;
            HouseState outputHouse = (HouseState)output;

            if (!(inputHouse.getAddress().equals(outputHouse.getAddress()))) {
                throw new IllegalArgumentException("En una transferencia de propiedad la dirección no debe cambiar");
            }

            if ((inputHouse.getOwner().equals(outputHouse.getOwner()))) {
                throw new IllegalArgumentException("En una transferencia el dueño de la propiedad debe cambiar");
            }

            // 3. Firmantes requeridos
            Party inputOwner = inputHouse.getOwner();
            Party outputOwner = outputHouse.getOwner();

            if (!(requiredSigners.contains(inputOwner.getOwningKey()))) {
                throw new IllegalArgumentException("El propietario debe firmar la transferencia");
            }

            if (!(requiredSigners.contains(outputOwner.getOwningKey()))) {
                throw new IllegalArgumentException("El nuevo propietario debe firmar la transferencia");
            }

        } else {
            throw new IllegalArgumentException("No se reonoce el comando.");
        }

    }

    public static class Register implements CommandData {}
    public static class Transfer implements CommandData {}
}
