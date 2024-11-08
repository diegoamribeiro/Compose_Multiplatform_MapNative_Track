import UIKit

class KeyboardActionHandlerIos: KeyboardActionHandler {
    func hideKeyboard() {
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}