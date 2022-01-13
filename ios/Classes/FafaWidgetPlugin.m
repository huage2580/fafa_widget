#import "FafaWidgetPlugin.h"
#if __has_include(<fafa_widget/fafa_widget-Swift.h>)
#import <fafa_widget/fafa_widget-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "fafa_widget-Swift.h"
#endif

@implementation FafaWidgetPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFafaWidgetPlugin registerWithRegistrar:registrar];
}
@end
