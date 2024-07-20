package zone.rong.loliasm.api;

import java.util.HashMap;

public class ResourceCache extends HashMap<String, byte[]> {

    public byte[] add(String s, byte[] bytes) {
        return super.put(s, bytes);
    }

    @Override
    public byte[] put(String s, byte[] bytes) {
        return bytes;
    }
}
