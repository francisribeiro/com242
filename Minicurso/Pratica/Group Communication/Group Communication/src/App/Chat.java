package App;

import org.jgroups.*;
import org.jgroups.util.Util;

import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class Chat extends ReceiverAdapter {

    private JChannel channel;
    private final Controller controller;
    private final List<String> state = new LinkedList<>();

    public Chat(Controller controller) {
        this.controller = controller;
    }

    // Cria um um grupo com base no nome
    public void start(String groupName) throws Exception {
        channel = new JChannel("udp.xml");
        channel.setReceiver(this);
        channel.connect(groupName); // Todos os canais que chamam connect()com o mesmo nome formam um cluster

        View view = channel.getView(); // Lista dos membros atuais de um grupo
        List<Address> address = view.getMembers();

        for (Address adr : address)
            System.out.println("Membros: " + adr.toString());

        channel.getState(null, 10000);
    }

    // Envia uma mensagem por multicast para todos os mebros no canal
    public void sendMessage(String message) throws Exception {
        channel.send(new Message(null, null, message));
    }

    // Invocado assim que uma mensagem for recebida
    @Override
    public void receive(Message msg) {
        String message = msg.getObject().toString();
        controller.getTextArea().appendText(message + "\n");

        synchronized (state) {
            state.add(message);
        }
    }

    // Notifica o destinatário de que um novo membro ingressou no grupo ou que um membro existente saiu
    @Override
    public void viewAccepted(View newView) {
        System.out.println("Membros: " + newView);
    }

    // Invocado sempre que um membro é suspeito de ter deixado de funcionar, mas ainda não foi excluído
    @Override
    public void suspect(Address adr) {
        try {
            sendMessage("Um membro foi desconectado por uma falha de rede!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Retorna o estado de um membro para gravar no fluxo de saída
    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (state) {
            Util.objectToStream(state, new DataOutputStream(output)); // Cria objeto a partir do fluxo de saída
        }
    }

    // Utilitário para ler o estado do fluxo de entrada e atribuí-lo à sua lista interna
    @Override
    public void setState(InputStream input) throws Exception {
        List<String> list = (List<String>) Util.objectFromStream(new DataInputStream(input)); // Cria objeto a partir do fluxo de entrada

        synchronized (state) {
            state.clear();
            state.addAll(list);
        }
    }

    // Faz a desconexão do canal
    public void disconect() throws Exception {
        View view = channel.getView();

        if (view.size() == 1) {
            channel.disconnect();
            closeChannel();
        } else
            channel.disconnect();
    }

    // Destrói a instancia de um canal, pilha de protocolos e libera recursos
    private void closeChannel() throws Exception {
        if (channel.isOpen()) {
            state.clear();
            channel.close();
        }
    }
}
