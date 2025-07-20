import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all) // Ignorar todas as safe areas para que o Compose gerencie
            .background(Color.clear) // Garantir que o background seja transparente
        .preferredColorScheme(nil) // Allow automatic color scheme detection
        .onAppear {
            // Configure status bar appearance based on system theme
            configureStatusBar()
        }
        .onChange(of: colorScheme) { _ in
            // Update status bar when system theme changes
            configureStatusBar()
        }
    }

    private func configureStatusBar() {
        DispatchQueue.main.async {
            if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
               let window = windowScene.windows.first {

                // Set status bar style based on current color scheme
                let statusBarStyle: UIStatusBarStyle = colorScheme == .dark ? .lightContent : .darkContent

                // Configure the window's status bar appearance
                if let statusBarManager = windowScene.statusBarManager {
                    // The status bar appearance is handled automatically by the system
                    // when UIUserInterfaceStyle is set to Automatic in Info.plist
                }
            }
        }
    }
}
