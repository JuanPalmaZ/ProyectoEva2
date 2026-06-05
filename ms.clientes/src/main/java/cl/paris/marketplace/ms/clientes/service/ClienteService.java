package cl.paris.marketplace.ms.clientes.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.clientes.dto.MetodoPagoRequest;
import cl.paris.marketplace.ms.clientes.dto.MetodoPagoResponse;
import cl.paris.marketplace.ms.clientes.mapper.ClienteMapper;
import cl.paris.marketplace.ms.clientes.model.MetodoPago;
import cl.paris.marketplace.ms.clientes.repository.MetodoPagoRepository;

@Service
public class ClienteService {

    private final MetodoPagoRepository metodoPagoRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(MetodoPagoRepository metodoPagoRepository, ClienteMapper clienteMapper) {
        this.metodoPagoRepository = metodoPagoRepository;
        this.clienteMapper = clienteMapper;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: MÉTODOS DE PAGO
    // ==========================================
    
    @Transactional
    public MetodoPagoResponse agregarMetodoPago(MetodoPagoRequest request, UUID usuarioId) { // Recibimos el ID del token
        
        // Transformar DTO a Entidad
        MetodoPago metodoPago = clienteMapper.toMetodoPagoEntity(request);
        
        // Le asignamos el ID seguro a la entidad
        metodoPago.setUsuarioId(usuarioId); 
        
        // Guardar en la base de datos
        MetodoPago metodoPagoGuardado = metodoPagoRepository.save(metodoPago);
        
        // Retornar la respuesta
        return clienteMapper.toMetodoPagoResponse(metodoPagoGuardado);
    }

    @Transactional(readOnly = true)
    public List<MetodoPagoResponse> listarMetodosPagoUsuario(UUID usuarioId) {
        return metodoPagoRepository.findByUsuarioId(usuarioId).stream()
                .map(clienteMapper::toMetodoPagoResponse)
                .toList(); 
    }
}