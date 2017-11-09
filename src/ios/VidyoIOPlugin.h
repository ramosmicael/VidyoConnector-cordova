#import <Cordova/CDVPlugin.h>

@class  VidyoViewController;

@interface VidyoIOPlugin : CDVPlugin

@property (nonatomic, retain) VidyoViewController* vidyoViewController;

- (void)launchVidyoIO:(CDVInvokedUrlCommand *)command;


- (void)destroy;

@end
