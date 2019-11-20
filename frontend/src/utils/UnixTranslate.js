import moment from 'moment';
import 'moment/locale/nb';

export const readableTime = (unixTime, pinpointTime = false) => {
    let text = ''

    const now = moment().locale('nb')
    const then = moment.unix(unixTime) // .utcOffset("+0100"))

    const nowDayStart = moment(now.format('YYYY-MM-DD'))
    const thenDayStart = moment(then.format('YYYY-MM-DD'))
    const yesterdayStart = nowDayStart.clone().subtract(1, 'day')
    const thenTime = then.format('HH:mm')

    const isSameDay = nowDayStart.unix() === thenDayStart.unix()
    const isYesterday = !isSameDay && then.unix() > yesterdayStart.unix()

    const diffMoment = now.diff(then)
    const daysBetweenEx = moment.duration(diffMoment).asDays()
    const daysBetween = Math.floor(daysBetweenEx)

    if (isSameDay || isYesterday) {
        const hoursBetween = moment.duration(diffMoment).asHours() // Math.abs(now.unix() - then.unix()) / 36e3;
        if (isSameDay) {
            text = pinpointTime ? 'I dag' : thenTime
        } else if (hoursBetween <= 6) {
            // Akkurat begynt på en ny dag!
            text = pinpointTime ? 'I går' : thenTime
        } else {
            // isYesterday
            text = 'I går'
        }
    } else if (daysBetween < 7) {
        text = then.format('dddd').substr(0, 3)
    } else {
        const day = then.format('D')
        const month = then.format('MMM').substr(0, 3)
        const year = then.format('Y')

        text = `${day} . ${month}`

        const sameYear = then.year() === moment().year()
        if (!sameYear) {
            text += ` ${year}`
        }

        text = text.toLowerCase()
    }

    const suffix = pinpointTime ? ` kl ${thenTime}` : ''
    return text + suffix
}