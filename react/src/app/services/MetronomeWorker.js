// the timer ID of the interval
window.self.timerID = null

// the interval time in milliseconds
window.self.interval = 100

// event handler for Worker.postMessage
window.self.onmessage = ({ data }) => {
    switch (data.message) {
        case "start":
            window.self.start()
            break
        case "stop":
            window.self.stop()
            break
        case "interval":
            window.self.changeInterval(data.interval)
            break
        case "close":
            window.self.stop()
            window.self.close()
            break
    }
}

// starts the timer
window.self.start = () => {
    window.self.timerID = setInterval(window.self.tick, window.self.interval)
}

// stops the interval
window.self.stop = () => {
    clearInterval(window.self.timerID)
    window.self.timerID = null
}

// stops the timer, changes the interval and starts timer again
window.self.changeInterval = (interval) => {
    window.self.interval = interval
    if (window.self.timerID) {
        window.self.stop()
        window.self.start()
    }
}

// posts the tick message
window.self.tick = () => {
    postMessage("tick")
}