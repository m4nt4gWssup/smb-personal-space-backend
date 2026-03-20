package ru.dis.personalspace.factory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dis.personalspace.enums.FavouriteType;
import ru.dis.personalspace.factory.impl.ArticleProcessor;
import ru.dis.personalspace.factory.impl.FaqProcessor;
import ru.dis.personalspace.factory.impl.NewsProcessor;
import ru.dis.personalspace.factory.impl.NormDocProcessor;

import java.util.EnumMap;
import java.util.Map;

@Component
public class FavouritesProcessorFactory {
    private final Map<FavouriteType, FavouritesProcessor> processors;

    @Autowired
    public FavouritesProcessorFactory(
            ArticleProcessor articleProcessor,
            NewsProcessor newsProcessor,
            NormDocProcessor normDocProcessor,
            FaqProcessor faqProcessor) {

        processors = new EnumMap<>(FavouriteType.class);
        processors.put(FavouriteType.ARTICLE, articleProcessor);
        processors.put(FavouriteType.NEWS, newsProcessor);
        processors.put(FavouriteType.NORM_DOC, normDocProcessor);
        processors.put(FavouriteType.FAQ, faqProcessor);
    }

    public FavouritesProcessor getProcessor(FavouriteType type) {
        return processors.get(type);
    }
}