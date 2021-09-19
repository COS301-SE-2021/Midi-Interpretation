import AFlat from "../../styles/images/keys/Ab.svg"
import A from "../../styles/images/keys/A.svg"
import BFlat from "../../styles/images/keys/Bb.svg"
import B from "../../styles/images/keys/B.svg"
import CFlat from "../../styles/images/keys/Cb.svg"
import C from "../../styles/images/keys/C.svg"
import CSharp from "../../styles/images/keys/C#.svg"
import DFlat from "../../styles/images/keys/Db.svg"
import D from "../../styles/images/keys/D.svg"
import EFlat from "../../styles/images/keys/Eb.svg"
import E from "../../styles/images/keys/E.svg"
import F from "../../styles/images/keys/F.svg"
import FSharp from "../../styles/images/keys/F.svg"
import GFlat from "../../styles/images/keys/Gb.svg"
import G from "../../styles/images/keys/G.svg"

/**
 * KeySignature to svg map
 */
class KeySignature{
    constructor() {
        this.imageMap = {
            'Cbmaj': CFlat,
            'Gbmaj': GFlat,
            'Dbmaj': DFlat,
            'Abmaj': AFlat,
            'Ebmaj': EFlat,
            'Bbmaj': BFlat,
            'Fmaj': F,
            'Cmaj': C,
            'Gmaj': G,
            'Dmaj': D,
            'Amaj': A,
            'Emaj': E,
            'Bmaj': B,
            'F#maj': FSharp,
            'C#maj': CSharp,
            'Abmin': CFlat,
            'Ebmin': GFlat,
            'Bbmin': DFlat,
            'Fmin': AFlat,
            'Cmin': EFlat,
            'Gmin': BFlat,
            'Dmin': F,
            'Amin': C,
            'Emin': G,
            'Bmin': D,
            'F#min': A,
            'C#min': E,
            'G#min': B,
            'D#min': FSharp,
            'A#min': CSharp,
        }
    }

    /**
     * getlinkForKey
     * @param key
     * @returns {*}
     */
    getLinkForKey(key){
        if(typeof this.imageMap[key] === 'undefined')
            return this.imageMap['Cmaj']
        return this.imageMap[key]
    }

}

export default KeySignature