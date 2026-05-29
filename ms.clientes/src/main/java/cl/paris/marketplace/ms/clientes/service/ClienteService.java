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

    // Inyección por constructor limpia y directa
    public ClienteService(MetodoPagoRepository metodoPagoRepository, ClienteMapper clienteMapper) {
        this.metodoPagoRepository = metodoPagoRepository;
        this.clienteMapper = clienteMapper;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: MÉTODOS DE PAGO
    // ==========================================
    
    @Transactional
    public MetodoPagoResponse agregarMetodoPago(MetodoPagoRequest request) {
        
        // Transformar DTO a Entidad usando tu nuevo Mapper limpio.
        // Nota Arquitectónica: Como el Usuario vive en otro microservicio, aquí ya no 
        // hacemos un "usuarioRepository.findById()". Simplemente guardamos el UUID 
        // asumiendo que el Frontend o el Gateway ya validaron que el usuario existe.
        MetodoPago metodoPago = clienteMapper.toMetodoPagoEntity(request);
        
        // Guardar en la base de datos
        MetodoPago metodoPagoGuardado = metodoPagoRepository.save(metodoPago);
        
        // Retornar la respuesta
        return clienteMapper.toMetodoPagoResponse(metodoPagoGuardado);
    }

    @Transactional(readOnly = true)
    public List<MetodoPagoResponse> listarMetodosPagoUsuario(UUID usuarioId) {
        // Busca todas las tarjetas de un usuario usando la Soft Foreign Key
        return metodoPagoRepository.findByUsuarioId(usuarioId).stream()
                .map(clienteMapper::toMetodoPagoResponse)
                .toList(); // Usando toList() nativo de Java moderno
    }
}