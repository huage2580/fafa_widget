
import 'dart:async';

import 'package:flutter/services.dart';

class FafaWidget {
  static const MethodChannel _channel = MethodChannel('fafa_widget');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String?> update() async {
    final String? version = await _channel.invokeMethod('update');
    return version;
  }

  static Future<String?> light() async{
    final String? version = await _channel.invokeMethod('statusBarIconLight');
    return version;
  }

  static Future<String?> dark() async{
    final String? version = await _channel.invokeMethod('statusBarIconDark');
    return version;
  }
}
