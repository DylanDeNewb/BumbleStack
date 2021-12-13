package net.bumblecraft.bumblestack.api.database;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.bumblecraft.bumblestack.spawners.BumbleSpawnerData;

import java.lang.reflect.Type;

public class Serialize {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
        @Override
        public JsonElement serialize(Double aDouble, Type type, JsonSerializationContext jsonSerializationContext) {
            Integer value = (int) Math.round(aDouble);
            return new JsonPrimitive(value);
        }
    }).create();

    public static final TypeToken<BumbleSpawnerData> spawnerDataToken = new TypeToken<BumbleSpawnerData>() {
    };

}
