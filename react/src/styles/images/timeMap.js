import time_0 from "../images/times/0.svg"
import time_1 from "../images/times/1.svg"
import time_2 from "../images/times/2.svg"
import time_3 from "../images/times/3.svg"
import time_4 from "../images/times/4.svg"
import time_5 from "../images/times/5.svg"
import time_6 from "../images/times/6.svg"
import time_7 from "../images/times/7.svg"
import time_8 from "../images/times/8.svg"
import time_9 from "../images/times/9.svg"
import time_10 from "../images/times/10.svg"
import time_11 from "../images/times/11.svg"
import time_12 from "../images/times/12.svg"
import time_13 from "../images/times/13.svg"
import time_14 from "../images/times/14.svg"
import time_16 from "../images/times/16.svg"
import time_17 from "../images/times/17.svg"
import time_18 from "../images/times/18.svg"
import time_32 from "../images/times/32.svg"

/***
 *  This class provides a mapping between SVG elements and their links for time signatures.
 *
 *  Under the Attribution-Share Alike 4.0 International licence (https://creativecommons.org/licenses/by-sa/4.0/deed.en)
 *  credit is given to Madelgarius (https://commons.wikimedia.org/wiki/User:Madelgarius) for all the images utilised by
 *  this module, which are not in any way modified.
 */

class TimeSignature{
    constructor() {
        this.timeMap = {
            0: time_0,
            1: time_1,
            2: time_2,
            3: time_3,
            4: time_4,
            5: time_5,
            6: time_6,
            7: time_7,
            8: time_8,
            9: time_9,
            10: time_10,
            11: time_11,
            12: time_12,
            13: time_13,
            14: time_14,
            16: time_16,
            17: time_17,
            18: time_18,
            32: time_32,
        }
    }

    getLinkForTime(time){
        if(typeof this.timeMap[time] === 'undefined')
            return this.timeMap[1]
        return this.timeMap[time]
    }
}

export default TimeSignature