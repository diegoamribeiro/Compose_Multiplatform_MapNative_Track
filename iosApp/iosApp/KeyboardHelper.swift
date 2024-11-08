import UIKit

class KeyboardHelper: NSObject {
    @objc static func hideKeyboard() { // Adicione @objc aqui
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}