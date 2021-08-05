package com.noxception.midisense.intelligence.strategies;

import com.noxception.midisense.intelligence.dataclass.GenrePredication;

public interface GenreAnalysisStrategy {

    int totalSuggestions = 10;
    int classificationClasses = 100;
    String[] classes = {"rock", "classic rock", "album rock", "hard rock", "permanent wave", "mellow gold", "alternative metal", "soft rock", "alternative rock", "metal", "pop rock", "art rock", "nu metal", "dance pop", "country rock", "folk rock", "new wave pop", "country", "pop", "europop", "adult standards", "punk", "progressive rock", "symphonic rock", "blues rock", "modern rock", "new wave", "psychedelic rock", "post-grunge", "glam rock", "funk metal", "rap metal", "new romantic", "power metal", "folk", "neo classical metal", "synthpop", "singer-songwriter", "progressive metal", "skate punk", "rap rock", "pop punk", "country road", "brill building pop", "dance rock", "post-teen pop", "grunge", "roots rock", "british invasion", "beatlesque", "thrash metal", "glam metal", "contemporary country", "socal pop punk", "old school thrash", "funk rock", "zolo", "lounge", "soul", "piano rock", "heartland rock", "symphonic metal", "rock-and-roll", "yacht rock", "canadian pop", "urban contemporary", "rockabilly", "jazz fusion", "instrumental rock", "neo mellow", "speed metal", "industrial metal", "bubblegum pop", "gothic metal", "easy listening", "pop rap", "death metal", "merseybeat", "hip hop", "art pop", "motown", "nashville sound", "disco", "quiet storm", "lilith", "country dawn", "melodic metal", "melancholia", "rap", "r&b", "german metal", "funk", "classic canadian rock", "ska punk", "britpop", "finnish metal", "classic country pop", "industrial rock", "groove metal","vocal jazz"};

    public GenrePredication[] classify(byte[] features);
}
