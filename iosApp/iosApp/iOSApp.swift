import SwiftUI
import GoogleMaps
import GooglePlaces


@main
struct iOSApp: App {

    init() {
        if let apiKey = Bundle.main.object(forInfoDictionaryKey: "MAPS_API_KEY") as? String, !apiKey.isEmpty {
            GMSServices.provideAPIKey(apiKey)
            GMSPlacesClient.provideAPIKey(apiKey)
            print("API Key loaded successfully")
        } else {
            print("API Key not found in Info.plist")
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
