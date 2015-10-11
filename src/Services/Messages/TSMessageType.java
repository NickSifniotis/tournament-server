package Services.Messages;

/**
 * Created by nsifniotis on 15/09/15.
 *
 * Cadeseus is a winged staff with two snakes wrapped around it.
 * It was an ancient astrological symbol of commerce and is associated
 * with the Greek god Hermes, **the messenger for the gods**,
 * conductor of the dead and protector of merchants and thieves.
 *
 * (emphasis mine)
 *
 * 05/10/2015 - Code refactored to work with new services model
 * Sorry, this means that the mythological references are out.
 */

public enum TSMessageType
{
    END, KILL_GAME, KILL_TOURNAMENT, THREAD_POOL_RESIZE, ADD_VIEWER
}
