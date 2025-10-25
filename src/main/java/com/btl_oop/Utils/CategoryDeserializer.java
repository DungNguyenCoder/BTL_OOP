package com.btl_oop.Utils;

import com.btl_oop.Model.Enum.Category;
import com.google.gson.*;
import java.lang.reflect.Type;

public class CategoryDeserializer implements JsonDeserializer<Category> {

    @Override
    public Category deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        try {
            // Trường hợp 1: category là String đơn giản: "SNACK"
            if (json.isJsonPrimitive()) {
                String categoryStr = json.getAsString().toUpperCase();
                return Category.valueOf(categoryStr);
            }

            // Trường hợp 2: category là Object: {"name": "SNACK", "displayName": "Snack"}
            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();

                // Lấy giá trị từ field "name"
                if (obj.has("name")) {
                    String categoryName = obj.get("name").getAsString().toUpperCase();
                    return Category.valueOf(categoryName);
                }

                // Hoặc lấy từ field "displayName" nếu không có "name"
                if (obj.has("displayName")) {
                    String displayName = obj.get("displayName").getAsString().toUpperCase();
                    return Category.valueOf(displayName);
                }
            }

            // Default fallback
            System.err.println("⚠️ Unknown category format: " + json);
            return Category.SNACK;

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Invalid category value: " + json);
            e.printStackTrace();
            return Category.SNACK; // default
        } catch (Exception e) {
            System.err.println("❌ Error deserializing category: " + e.getMessage());
            e.printStackTrace();
            return Category.SNACK;
        }
    }
}