resolvers in ThisBuild += "lightbend-commercial-mvn" at "https://repo.lightbend.com/pass/ErmwX6tBsDChkDnqhNKFVAU_TDlJVYZMKUbUZvmjoN8EJEjG/commercial-releases"
resolvers in ThisBuild += Resolver.url("lightbend-commercial-ivy", url("https://repo.lightbend.com/pass/ErmwX6tBsDChkDnqhNKFVAU_TDlJVYZMKUbUZvmjoN8EJEjG/commercial-releases"))(Resolver.ivyStylePatterns)