package com.srichell.microservices.ratelimit.pojos;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RoomInfo {
    private final long hotelId;
    private final RoomType roomType;
    private final float price;

    public RoomInfo(long hotelId, String roomType, float price) {
        this.hotelId = hotelId;
        this.roomType = RoomType.getByType(roomType);
        this.price = price;
    }

    private enum RoomType {
        DELUXE_ROOM("Deluxe"),
        SUPERIOR_ROOM("Superior"),
        SWEET_SUITE_ROOM("Sweet Suite"),
        ;

        RoomType(String roomType) {
            this.roomType = roomType;
        }

        private String roomType;
        public String getRoomType() {
            return roomType;
        }


        public static RoomInfo.RoomType getByType(String roomType) {
            for (RoomInfo.RoomType room : RoomInfo.RoomType.values()) {
                if (room.getRoomType().equalsIgnoreCase(roomType)) {
                    return room;
                }
            }
            return null;
        }
    }

}
