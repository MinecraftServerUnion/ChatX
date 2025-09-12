package dev.onelili.unichat.velocity.util;

import com.github.retrooper.packetevents.protocol.nbt.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Map;

@SuppressWarnings("NullableProblems")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static @Nonnull JsonObject getNBTJson(@Nonnull NBTCompound nbt) {
        JsonObject json = new JsonObject();
        for(Map.Entry<String, NBT> entry : nbt.getTags().entrySet()) {
            if(entry.getValue().getType().equals(NBTType.COMPOUND))
                json.add(entry.getKey(), getNBTJson((NBTCompound) entry.getValue()));
            else if(entry.getValue().getType().equals(NBTType.BYTE))
                json.addProperty(entry.getKey(), ((NBTByte) entry.getValue()).getAsByte());
            else if(entry.getValue().getType().equals(NBTType.DOUBLE))
                json.addProperty(entry.getKey(), ((NBTDouble) entry.getValue()).getAsDouble());
            else if(entry.getValue().getType().equals(NBTType.INT))
                json.addProperty(entry.getKey(), ((NBTInt) entry.getValue()).getAsInt());
            else if(entry.getValue().getType().equals(NBTType.FLOAT))
                json.addProperty(entry.getKey(), ((NBTFloat) entry.getValue()).getAsFloat());
            else if(entry.getValue().getType().equals(NBTType.SHORT))
                json.addProperty(entry.getKey(), ((NBTShort) entry.getValue()).getAsShort());
            else if(entry.getValue().getType().equals(NBTType.LONG))
                json.addProperty(entry.getKey(), ((NBTLong) entry.getValue()).getAsLong());
            else if(entry.getValue().getType().equals(NBTType.BYTE_ARRAY)) {
                JsonArray array = new JsonArray();
                for(byte b : ((NBTByteArray) entry.getValue()).getValue())
                    array.add(b);
                json.add(entry.getKey(), array);
            } else if(entry.getValue().getType().equals(NBTType.INT_ARRAY)) {
                JsonArray array = new JsonArray();
                for(int i : ((NBTIntArray) entry.getValue()).getValue())
                    array.add(i);
                json.add(entry.getKey(), array);
            } else if(entry.getValue().getType().equals(NBTType.LONG_ARRAY)) {
                JsonArray array = new JsonArray();
                for(long l : ((NBTLongArray) entry.getValue()).getValue())
                    array.add(l);
                json.add(entry.getKey(), array);
            } else if(entry.getValue().getType().equals(NBTType.STRING))
                json.addProperty(entry.getKey(), ((NBTString) entry.getValue()).getValue());
            else if(entry.getValue().getType().equals(NBTType.LIST))
                json.add(entry.getKey(), getNBTListJson((NBTList<? extends NBT>) entry.getValue()));
        }
        return json;
    }

    public static <T extends NBT> @Nonnull JsonArray getNBTListJson(@Nonnull NBTList<T> nbt) {
        JsonArray array = new JsonArray();
        for(NBT value : nbt.getTags()) {
            if(value.getType().equals(NBTType.COMPOUND))
                array.add(getNBTJson((NBTCompound) value));
            else if(value.getType().equals(NBTType.BYTE))
                array.add(((NBTByte) value).getAsByte());
            else if(value.getType().equals(NBTType.DOUBLE))
                array.add(((NBTDouble) value).getAsDouble());
            else if(value.getType().equals(NBTType.INT))
                array.add(((NBTInt) value).getAsInt());
            else if(value.getType().equals(NBTType.FLOAT))
                array.add(((NBTFloat) value).getAsFloat());
            else if(value.getType().equals(NBTType.SHORT))
                array.add(((NBTShort) value).getAsShort());
            else if(value.getType().equals(NBTType.LONG))
                array.add(((NBTLong) value).getAsLong());
            else if(value.getType().equals(NBTType.BYTE_ARRAY)) {
                JsonArray array1 = new JsonArray();
                for(byte b : ((NBTByteArray) value).getValue())
                    array1.add(b);
                array.add(array1);
            } else if(value.getType().equals(NBTType.INT_ARRAY)) {
                JsonArray array1 = new JsonArray();
                for(int i : ((NBTIntArray) value).getValue())
                    array1.add(i);
                array.add(array1);
            } else if(value.getType().equals(NBTType.LONG_ARRAY)) {
                JsonArray array1 = new JsonArray();
                for(long l : ((NBTLongArray) value).getValue())
                    array1.add(l);
                array.add(array1);
            } else if(value.getType().equals(NBTType.STRING))
                array.add(((NBTString) value).getValue());
            else if(value.getType().equals(NBTType.LIST))
                array.add(getNBTListJson((NBTList<? extends NBT>) value));
        }
        return array;
    }
}
