package com.coelacanthe.dto;

import java.util.Map;

public record NotificationRequest(String titre,

                                  String message,

                                  String imageUrl,

                                  Map<String, String> data) {

}
