package flying.grub.securekey.network;

/**
 * Created by fly on 9/10/15.
 */
public interface DoorStateChanger {
    void closed();
    void disconnected();
    void opened();
    void connected();
}
