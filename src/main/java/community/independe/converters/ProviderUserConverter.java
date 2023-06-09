package community.independe.converters;

public interface ProviderUserConverter<T, R> {

    R converter(T t);
}
