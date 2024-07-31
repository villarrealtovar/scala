package com.javt.datasoft.config

import pureconfig.ConfigReader
import pureconfig.generic.derivation.ConfigReaderDerivation.Default.derived
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import pureconfig.error.CannotConvert

// generates: given configReader: ConfigReader[EmberConfig]
final case class EmberConfig(host: Host, port: Port) derives ConfigReader

object EmberConfig {
    // need given ConfigReader[Host] + given ConfigReader[Port] => compiler generates ConfigReader[EmberConfig]
    
    given hostReader: ConfigReader[Host] = ConfigReader[String].emap { hostString =>
        Host.fromString(hostString) match
            case None => Left(CannotConvert(hostString, Host.getClass.toString, s"Invalid host string: `$hostString`"))
            case Some(host) => Right(host)
        
    }

    given portReader: ConfigReader[Port] = ConfigReader[Int].emap { portInt =>
        Port
            .fromInt(portInt)
            .toRight(CannotConvert(portInt.toString, Port.getClass.toString, s"Invalid port number: `$portInt`"))
    }

}