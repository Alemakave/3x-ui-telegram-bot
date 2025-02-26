package ru.alemakave.xuitelegrambot.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.alemakave.xuitelegrambot.dto.ClientAddDto;
import ru.alemakave.xuitelegrambot.dto.ClientAddSettingsDto;
import ru.alemakave.xuitelegrambot.dto.ClientUpdateDto;
import ru.alemakave.xuitelegrambot.dto.ClientUpdateSettingsDto;
import ru.alemakave.xuitelegrambot.model.Client;

import java.util.List;

@Mapper(componentModel = "spring", imports = {List.class, ClientAddSettingsDto.class, ClientUpdateSettingsDto.class, ObjectMapper.class})
public abstract class ClientMapper {
    @Mapping(target = "id", expression = "java(connectionId)")
    @Mapping(target = "settings", expression = "java(new ObjectMapper().writeValueAsString(new ClientAddSettingsDto(List.of(client))))")
    public abstract ClientAddDto clientToClientAddDto(long connectionId, Client client) throws JsonProcessingException;
}
