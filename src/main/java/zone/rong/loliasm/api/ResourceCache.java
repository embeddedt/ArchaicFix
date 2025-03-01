package zone.rong.loliasm.api;

import java.util.HashMap;

public class ResourceCache extends HashMap<String, byte[]> {

    private static final long serialVersionUID = 3922779088970477966L;

    public byte[] add(String s, byte[] bytes) {
        return super.put(s, bytes);
    }

    @Override
    public byte[] put(String s, byte[] bytes) {
        return bytes;
    }
}
